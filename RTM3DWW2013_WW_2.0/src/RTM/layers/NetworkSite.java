/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.layers;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL2;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * RTM 2.0 Change all references to GL to GL2
 * @author Janusz Martyniak
 */
public class NetworkSite implements GenericSite, Pickable {

    private final int max = 20; // hardcoded here and in RealTimeMonitor.Pulser - fix later
    private String id = "";
    private String name = "";
    private String longname = "";
    private double latitude = 0;
    private double longitude = 0;
    private String location = ""; //i.e London
    private String country = "";
    protected int rateOut = 0;
    protected int rateIn = 0;
    protected int degree_break = 0;
    protected double zoom = 0;
    protected Vec4 p = null;
    protected double x = 0;
    protected double y = 0;
    protected double z = 0;
    protected double theta = 0;
    protected double phi = 0;
    protected double ce_scale_factor = 1;
    private CircleSize cs = new CircleSize();
    private boolean first = true;
    protected double[][] coordinates = null;
    private String trafficPlot;
    private JLabel logoLabel;
    private String interfaceName;
    private String hostname;
    private String ifaceBW;
    private Vector<Double> rates = new Vector<Double>(2);
    private long timestamp;
    private String website;
    private String ifaceName;
    private String archiveName;

    public NetworkSite() {
        rates.add(new Double(0.));
        rates.add(new Double(0.));
    }

    public void addMainInfo(String id, String name, String longname, String latitude, String longitude, String country) {
        this.id = id;
        this.name = name;
        this.longname = longname;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.country = country;
    }

    public void addTrafficPlot(String plot) {
        this.trafficPlot = plot;
    }

    public void addLogo(final String logoAddress) {
        try {

            // EDT
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    try {
                        URL logoURL = new URL(logoAddress);
                        logoLabel = new JLabel();
                        Image logoImage = Toolkit.getDefaultToolkit().createImage(logoURL);
                        logoLabel.setIcon(new ImageIcon(logoImage));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(NetworkSite.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Cannot find logo?");
        }
    }

    public CircleSize getCS() {
        return this.cs;
    }

    public Position getPosition() {
        return new Position(Angle.fromDegrees(latitude), Angle.fromDegrees(longitude), 0.0);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void render(DrawContext dc) {

        ////O.a("rendering...");

        if (first) {
            addDrawInfo();
            first = false;
        }

        this.calculateZoom();

        //if (!(CEs.isEmpty() && RBs.isEmpty())) {  // irrelevant for network sites
        //O.a("1");

        GL2 gl = dc.getGL().getGL2();

        synchronized (gl) {

            gl.glDisable(GL2.GL_LIGHTING);
            gl.glDisable(GL2.GL_TEXTURE_2D);

            // enable polygon antialiasing
            gl.glDisable(GL2.GL_DEPTH_TEST);
            gl.glEnable(GL2.GL_LINE_SMOOTH);
            gl.glEnable(GL2.GL_POLYGON_SMOOTH);
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
            gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

            //if (!CEs.isEmpty()) { irrelevant for network sites
            //O.a("11a");

            this.calculateBreaks();
            if (degree_break > -1) {
                //O.a("111");

                double size = zoom * cs.get() * ce_scale_factor;
                if ((degree_break < 359) && (degree_break > 0)) {
                    //O.a("1111");

                    // Draw the CE(s) - overlap is correct
                    // running
                    gl.glColor4f(0.3f, 1.0f, 0.3f, 0.8f);
                    gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(x, y, z);
                    for (int i = 0; i <= degree_break; i++) {
                        gl.glVertex3d(x + size * coordinates[i][0], y + size * coordinates[i][1], z + size * coordinates[i][2]);
                    }
                    gl.glEnd();
                    // scheduled
                    gl.glColor4f(1.0f, 0.3f, 1.0f, 0.8f);
                    gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(x, y, z);
                    for (int i = degree_break; i < 360; i++) {
                        gl.glVertex3d(x + size * coordinates[i][0], y + size * coordinates[i][1], z + size * coordinates[i][2]);
                    }
                    gl.glVertex3d(x + size * coordinates[0][0], y + size * coordinates[0][1], z + size * coordinates[0][2]);
                    gl.glEnd();
                } else {
                    //O.a("1110");
                    if (degree_break == 359) {
                        //O.a("11101");
                        // only running
                        gl.glColor4f(0.3f, 1.0f, 0.3f, 0.8f);
                        gl.glBegin(GL2.GL_POLYGON);
                        for (int i = 0; i <= 359; i++) {
                            gl.glVertex3d(x + size * coordinates[i][0], y + size * coordinates[i][1], z + size * coordinates[i][2]);
                        }
                        gl.glEnd();
                    } else {
                        //O.a("11100");
                        // only scheduled
                        gl.glColor4f(1.0f, 0.3f, 1.0f, 0.8f);
                        gl.glBegin(GL2.GL_POLYGON);
                        for (int i = 0; i <= 359; i++) {
                            gl.glVertex3d(x + size * coordinates[i][0], y + size * coordinates[i][1], z + size * coordinates[i][2]);
                        }
                        gl.glEnd();
                    }
                }
            } else {
                //O.a("110");
                double size = zoom * 5;
                gl.glColor4f(1.0f, 1.0f, 0.3f, 0.8f);
                gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(x, y, z);
                for (int i = 0; i < 360; i++) {
                    gl.glVertex3d(x + size * coordinates[i][0], y + size * coordinates[i][1], z + size * coordinates[i][2]);
                }
                gl.glEnd();
            }
            // } else {
            //O.a("10a");
            // }
                /* dont do rotating triangles for this Site type
            if (!RBs.isEmpty() && thisRBChosen) {
            //O.a("11b");
            
            double size = zoom * 15;
            turn = turn + 3;
            if (turn == 120) {
            //O.a("111");
            turn = 0;
            } else {
            //O.a("110");
            }
            int pos1 = turn;
            int pos2 = turn + 120;
            int pos3 = turn + 240;
            gl.glColor4f(0.3f, 1.0f, 1.0f, 0.8f);
            gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glBegin(GL.GL_POLYGON);
            gl.glVertex3d(x + size * coordinates[pos1][0], y + size * coordinates[pos1][1], z + size * coordinates[pos1][2]);
            gl.glVertex3d(x + size * coordinates[pos2][0], y + size * coordinates[pos2][1], z + size * coordinates[pos2][2]);
            gl.glVertex3d(x + size * coordinates[pos3][0], y + size * coordinates[pos3][1], z + size * coordinates[pos3][2]);
            gl.glEnd();
            gl.glPopAttrib();
            } else {
            //O.a("10b");
            }
             */
            gl.glColor4f(1f, 1f, 1f, 1f);
            gl.glPopAttrib();
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_LINE_SMOOTH);
            gl.glDisable(GL2.GL_POLYGON_SMOOTH);
        }
        //} else {
        //O.a("0");
        //}
    }

    public void addDrawInfo() {

        p = RTM.RealTimeMonitor.getAf().getWwd().getModel().getGlobe().computePointFromPosition(Angle.fromDegrees(latitude), Angle.fromDegrees(longitude), 0.0);
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
        this.phi = Math.acos(z / java.lang.Math.sqrt(p.x * p.x + p.y * p.y + p.z * p.z));
        this.theta = Math.PI / 2 + Math.atan2(x, y);

        this.calculateBreaks();
        this.calculateCoords();
        this.calculateZoom();

    }

    public boolean closeEnough(Vec4 m) {
        boolean close = true;
        Position pp = RTM.RealTimeMonitor.getAf().getWwd().getModel().getGlobe().computePositionFromPoint(p);
        Position pm = RTM.RealTimeMonitor.getAf().getWwd().getModel().getGlobe().computePositionFromPoint(m);
        double deltaLatitude = Math.abs(pp.getLatitude().getDegrees() - pm.getLatitude().getDegrees());
        double deltaLongitude = Math.abs(pp.getLongitude().getDegrees() - pm.getLongitude().getDegrees());
        // zoom function seems to give ok values for this...
        close = (deltaLatitude < (zoom / 4)) ? close : false;
        close = (deltaLongitude < (zoom / 4)) ? close : false;
        return close;
    }

    public double distance(Vec4 m) {
        Position pp = RTM.RealTimeMonitor.getAf().getWwd().getModel().getGlobe().computePositionFromPoint(p);
        Position pm = RTM.RealTimeMonitor.getAf().getWwd().getModel().getGlobe().computePositionFromPoint(m);
        double deltaLatitude = Math.abs(pp.getLatitude().getDegrees() - pm.getLatitude().getDegrees());
        double deltaLongitude = Math.abs(pp.getLongitude().getDegrees() - pm.getLongitude().getDegrees());
        return Math.sqrt(deltaLatitude * deltaLatitude + deltaLongitude * deltaLongitude);
    }

    public synchronized String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLongName() {
        return longname;
    }

    public synchronized double getLatitude() {
        return latitude;
    }

    public synchronized double getLongitude() {
        return longitude;
    }

    public String getCountry() {
        return country;
    }

    private void calculateBreaks() {
        double drateIn = getRates().get(0).doubleValue();
        double drateOut = getRates().get(1).doubleValue();
        if ((drateOut + drateIn) > 0) {
            degree_break = (int) (359 * drateIn / (drateOut + drateIn));
            ce_scale_factor = Math.log10(drateIn);
            ce_scale_factor = (ce_scale_factor > 1) ? ce_scale_factor : 1;
        } else {
            degree_break = -1;
        }
    }
    // need better function?

    private void calculateZoom() {
        zoom = Math.sqrt(((BasicOrbitView) RTM.RealTimeMonitor.getAf().getWwd().getView()).getZoom()) / 800;
    }

    private void calculateCoords() {

        coordinates = new double[360][3];
        double radius = 750;
        for (int i = 0; i < 360; i++) {
            double degInRad = i * Math.PI / 180;
            double x_flat = Math.cos(degInRad) * radius;
            double y_flat = Math.sin(degInRad) * radius;
            coordinates[i][0] = x_flat * Math.cos(phi) * Math.cos(theta) + y_flat * Math.sin(theta);
            coordinates[i][1] = -1.0 * x_flat * Math.cos(phi) * Math.sin(theta) + y_flat * Math.cos(theta);
            coordinates[i][2] = x_flat * Math.sin(phi);
        }

    }

    /**
     * Show Site data on a SidePanel. Only if a site belongs to a layer which is enabled.
     */
    public void showPanel() {
        
        if (isLayerEnabled("Network Sites Layer")) {
            RTM.RealTimeMonitor.getSidePanel().addSite(this);
        }
    }

    /**
     * Test if a layer the site belongs to is enabled.
     * @return true if enabled, false otherwise.
     */
    public Boolean isLayerEnabled(String layer) {
        return RTM.RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName(layer).isEnabled();
    }

    public void showInFrame() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getGridGuide() {
        return website;
    }
    public String getHomepage() {
        return website;
    }

    public JLabel getLogoLabel() {
        return logoLabel;
    }

    public String getTrafficPlot() {
        return this.trafficPlot;
    }

    public void addInterfaceName(String string) {
        this.interfaceName = string;
    }

    public void addHostname(String name) {
        this.hostname = name;
    }

    public void addInterfaceBW(String i) {
        this.ifaceBW = i;
    }

    public void addIfaceName(String name) {
        this.ifaceName = name;
    }

    public synchronized void addRates(double bytesIn, double bytesOut) {
        this.rateIn = (int) bytesIn;
        this.rateOut = (int) bytesOut;
        this.rates = new Vector<Double>();
        this.rates.add(new Double(bytesIn));
        this.rates.add(new Double(bytesOut));
    }

    public void setTimestamp(long i) {
        this.timestamp = i;
    }

    public void setWebpage(String webpage) {
        website = webpage;
    }

    public String getInterfaceName() {
        return ifaceName;
    }

    public String getHostname() {
        return hostname;
    }

    public String getInterfaceBW() {
        return ifaceBW;
    }

    public synchronized Vector<Double> getRates() {
        return rates;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void addArchiveName(String archiveName) {
       this.archiveName = archiveName;
    }
    public String toString() {
        String s = "***NetworkSite: \n";
        s = s + "Name: " + this.getName() + "\n" + "Position: " + this.getPosition().toString() + state() + "\n***end";
        return s;
    }   
    private String state() {
        //return "unknown";
        return isLayerEnabled("Network Sites Layer") ? " (Enabled) " : " (Disabled) ";
    }
}
