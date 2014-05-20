/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.layers;

import RTM.datasource.filetransfer.FileTransferLink;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import java.util.ArrayList;
import java.util.HashMap;
import javax.media.opengl.GL2;
//import gov.nasa.worldwind.render.Cylinder;

/**
 * GL2 replaces GL
 * @author Janusz Martyniak
 */
public class DataTransferSite extends AbstractSite implements Pickable {

    private HashMap<String, CMSFileTransferSite> subsites = new HashMap<String, CMSFileTransferSite>();
    private CircleSize cs = new CircleSize();
    private boolean first = true;
    protected int degree_break = 0;

    /**
     * Return a list of CMS file transfer site for this physical site 
     * @return 
     */
    public HashMap<String, CMSFileTransferSite> getCMSFileTransferSites() {
        return subsites;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("Site: " + getName() + " Lat:" + getLatitude() + " Long:" + getLongitude()+"\n");
        if (getLatitude() == 0 || getLongitude() == 0) {
            buf.append("###### ZERO !!!!! ######");
        }
        double [] curRates = getRates().getRatesAsDoubles();
        buf.append("Rates: IN: "+curRates[0] + " OUT: "+ curRates[1] +"\n");
        buf.append(getCMSFileTransferSites().toString());
        return buf.toString();
    }
    public String getRatesAsSstring(){
        return getRates().toString();
    }
    // picking..

    public void showPanel() {

        if (isLayerEnabled("FTS Layer")) {
            RTM.RealTimeMonitor.getSidePanel().addSite(this);
            System.out.println(" Site " + this.getID() + " clicked !");
        }
    }

    public CircleSize getCS() {
        return this.cs;
    }

    private void drawCylinders() {
        RTMCylinder cyl = new RTMCylinder(this);
        Thread t = new Thread(cyl);
        t.start();
    }

    public void render(DrawContext dc) {



        if (first) {
            drawCylinders();
            RTMMarkerAttributes attrs = new RTMMarkerAttributes(Material.RED, BasicMarkerShape.SPHERE, 1d, 10, 10000, 60000);
            attrs.addCustomAttribute(this);
            Marker marker = new BasicMarker(Position.fromDegrees(getLatitude(), getLongitude(), 0), attrs);
            RTM.RealTimeMonitor.getFileTransferSitesMarkers().add(marker);
            addDrawInfo();
            first = false;
        }

        this.calculateZoom();

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
                /*
                //O.a("111");
                // pulsating size:   [0,10]    * rate_in
                double size = zoom * cs.get() * ce_scale_factor;
                //System.out.println(" site " + getName() + " size " + size);
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
                } */
            } else {
                // empty - a yellow cyrcle ?;
                /*
                double size = zoom * 5;
                gl.glColor4f(1.0f, 1.0f, 0.3f, 0.8f);
                gl.glBegin(GL2.GL_POLYGON);
                gl.glVertex3d(x, y, z);
                for (int i = 0; i < 360; i++) {
                gl.glVertex3d(x + size * coordinates[i][0], y + size * coordinates[i][1], z + size * coordinates[i][2]);
                }
                gl.glEnd();
                 */
            }

            gl.glColor4f(1f, 1f, 1f, 1f);
            gl.glPopAttrib();
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_LINE_SMOOTH);
            gl.glDisable(GL2.GL_POLYGON_SMOOTH);
        }
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

    private void calculateBreaks() {
        double drateIn = getRates().getRatesAsDoubles()[0];
        double drateOut = getRates().getRatesAsDoubles()[1];
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

    public Rates getRates() {
        return new Rates(this);

    }

    public void showInFrame() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public class Rates {
        /**
         * Holds global in/out rates from all sub sites and links
         * @param site 
         */
        public Rates(DataTransferSite site) {
            for (CMSFileTransferSite subsite : subsites.values()) {
                for (FileTransferLink value : subsite.getFromLink().values()) {
                    rates[0] = rates[0] + value.getTransfer().getFrate();
                    bytes[0] = bytes[0] + value.getTransfer().getIdone_bytes();
                }
                for (FileTransferLink value : subsite.getToLink().values()) {
                    rates[1] = rates[1] + value.getTransfer().getFrate();
                    bytes[1] = bytes[1] + value.getTransfer().getIdone_bytes();
                }
            }
        }
        public String toString() {
            return String.format(" rate in:  %10.2f kB/s%n rate out: %10.2f kB/s\n" , rates[0]/1E3, rates[1]/1E3);
        }
        public double[] getRatesAsDoubles() {
            return rates;
        }

        public long[] getDoneBytes() {
            return bytes;
        }
        private double[] rates = {0., 0.};
        private long[] bytes = {0, 0};
    }
}
