package RTM.layers;

//import RealTimeMonitor.RealTimeMonitor;
import RTM.config.Config;
import RTM.datasource.glite.Job;
import RTM.datasource.glite.JobStatisticsHandler;
import RTM.jaxb.jobstat.Ce;
import RTM.jaxb.jobstat.State;
import RTM.ui.RTMSiteInFrame;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL2;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import java.net.URL;

import java.awt.Toolkit;
import java.awt.Image;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

/**
 *   GL2 replaces GL (JM, June 2013)
 */
final public class Site implements Pickable, GenericSite {

    private final int max = 20; // hardcoded here and in RealTimeMonitor.Pulser - fix later
    private String id = "";
    private String name = "";
    private String longname = "";
    private double latitude = 0;
    private double longitude = 0;
    private String gridguide = "";
    private String country = "";
    private ArrayList<String> CEs = new ArrayList<String>(1);
    private ArrayList<String> RBs = new ArrayList<String>(1);
    private JLabel logoLabel;
    //private Hashtable<String, Object> overKill = null;
    private Hashtable<String, Job> GridCEJobs = new Hashtable<String, Job>();
    private Hashtable<String, Job> GridRBJobs = new Hashtable<String, Job>();
    // LinkedHashMap with access-order and max size.
    private Map<String, Job> LatestGridCEJobs;
    private Map<String, Job> LatestGridRBJobs;
    protected Vec4 p = null;
    protected double x = 0;
    protected double y = 0;
    protected double z = 0;
    protected double theta = 0;
    protected double phi = 0;
    protected int scheduledCE = 0;
    protected int runningCE = 0;
    protected int scheduledRB = 0;
    protected int runningRB = 0;
    protected int degree_break = 0;
    protected boolean thisRBChosen = true;
    protected double zoom = 0;
    protected double ce_scale_factor = 1;
    protected double[][] coordinates = null;
    protected int turn = 0;
    //private WorldWindowGLCanvas wwd = null;
    private CircleSize cs = new CircleSize();
    private boolean first = true;
    //private AppFrame af;
    private JInternalFrame inf;
    private final int N = 512;  // max number of jobs kept for statistics.
    //public SiteStats stats = new SiteStats();

    public Site() {
        LatestGridCEJobs = Collections.synchronizedMap(new LinkedHashMap<String, Job>(N / 2, 0.75f, true) {

            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                // we remove objects from Map ourselves and return false, as in
                // Java docs, to allow for some flexibility (for stats), but at the end..
                // stats are calculeated differently. This code defaults to
                // the standard LinkedHashMap behaviour. Might get rid of it
                // entirely.
                //   explicit:
                if (size() > N) {
                    remove((String) eldest.getKey());
                }
                return false;
            }

            @Override
            public Job remove(Object key) {
                Job oldJob = super.remove((String) key);
                if (isChosen(oldJob)) {
                    if (oldJob.getState().equals("Scheduled")) {
                        //jm scheduledCE--;
                    } else if (oldJob.getState().equals("Running")) {
                        //jm runningCE--;
                    }
                }

                return oldJob;
            }
        });

        LatestGridRBJobs = Collections.synchronizedMap(new LinkedHashMap<String, Job>(N / 2, 0.75f, true) {

            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                // we remove objects from Map ourselves and return false, as in the java docs.
                if (size() > N) {
                    remove((String) eldest.getKey());
                }
                return false;
                //    return size() > N;    implicit

            }

            @Override
            public Job remove(Object key) {
                Job oldJob = super.remove((String) key);
                if (isChosen(oldJob)) {
                    if (oldJob.getState().equals("Scheduled")) {
                        //scheduledRB--;
                    } else if (oldJob.getState().equals("Running")) {
                        //runningRB--;
                    }
                }
                return oldJob;
            }
        });
    }

    public void showInFrame() {
        if (inf != null) {
            inf.setVisible(true);
        } else {
            createInFrame();
        }
    }

    private void createInFrame() {
        this.inf = new RTMSiteInFrame(this);
//jm        RealTimeMonitor.getAf().getContentPane().add(inf, JLayeredPane.PALETTE_LAYER);
        RTM.RealTimeMonitor.getAf().add(inf, JLayeredPane.PALETTE_LAYER);
        inf.setVisible(true);
        this.inf.requestFocusInWindow();
    }

    public void nullFrame() {
        this.inf = null;
    }

    public CircleSize getCS() {
        return this.cs;
    }

    //removed after af was inserted
//    public void setWwd(WorldWindowGLCanvas wwd) {
//        this.wwd = wwd;
//    }
    public void addMainInfo(String id, String name, String longname, String latitude, String longitude, String country) {
        this.id = id;
        this.name = name;
        this.longname = longname;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.country = country;
    }

    public void addCE(String ip) {
        CEs.add(ip);
    }

    public void addRB(String ip) {
        RBs.add(ip);
    }

    public void addLogo(final String logoName) {
        try {

            // EDT
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    try {
                        String logoAddress = Config.getWS_URL() + "/logos/" + logoName + ".png";
                        URL logoURL = new URL(logoAddress);
                        logoLabel = new JLabel();
                        Image logoImage = Toolkit.getDefaultToolkit().createImage(logoURL);
                        logoLabel.setIcon(new ImageIcon(logoImage));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(Site.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Cannot find logo?");
        }
    }

    public void addGridGuide(String gridguide) {
        this.gridguide = gridguide;
    }

    public String getGridGuide() {
        return gridguide;
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

    /**
     * Check if a job is selected to display. Based on RB/VO selection.
     * @param job job to check.
     * @return true is chosen, false otherwise.
     */
    boolean isChosen(Job job) {
        boolean chosen = false;
        String rbChoices = RTM.RealTimeMonitor.getRBList();
        String voChoices = RTM.RealTimeMonitor.getVOList();
        // "all" case first:
        if (rbChoices.equals("all")) {
            chosen = true;
        } else {
            String thisRB = "-" + job.getRB() + "-";
            if (rbChoices.indexOf(thisRB) > 0) {
                chosen = true;
            }
        }
        if (chosen) {
            if (!voChoices.equals("all")) {
                String thisVO = "-" + job.getVO() + "-";
                if (!(voChoices.indexOf(thisVO) > 0)) {
                    chosen = false;
                }
            }
        }

        return chosen;
    }

    public void pushRBJob(Job job) {
        String id = job.getID();
        String rbChoices = RTM.RealTimeMonitor.getRBList();
        String voChoices = RTM.RealTimeMonitor.getVOList();
        synchronized (LatestGridRBJobs) {
            try {
                // always add the job, this returns the previous value, or null if there was no previous value.
                //Job oldJob = GridRBJobs.put(id, job);
                //test
                Job oldJob = LatestGridRBJobs.put(id, job);
                // now limit stats according to choices
                boolean chosen = false;
                if (rbChoices.equals("all")) {
                    chosen = true;
                } else {
                    String thisRB = "-" + oldJob.getRB() + "-";
                    if (rbChoices.indexOf(thisRB) > 0) {
                        chosen = true;
                    }
                }
                if (chosen) {
                    if (!voChoices.equals("all")) {
                        String thisVO = "-" + oldJob.getVO() + "-";
                        if (!(voChoices.indexOf(thisVO) > 0)) {
                            chosen = false;
                        }
                    }
                }

                if (chosen) {
                    String oldState = oldJob.getState();
                    if (oldState.equals("Scheduled")) {
                        scheduledRB--;
                    } else {
                        if (oldState.equals("Running")) {
                            runningRB--;
                        }
                    }
                }
            } catch (Exception e) {
            } // means there was no old value 

            boolean chosen = false;
            if (rbChoices.equals("all")) {
                chosen = true;
            } else {
                String thisRB = "-" + job.getRB() + "-";
                if (rbChoices.indexOf(thisRB) > 0) {
                    chosen = true;
                }
            }
            if (chosen) {
                if (!voChoices.equals("all")) {
                    String thisVO = "-" + job.getVO() + "-";
                    if (!(voChoices.indexOf(thisVO) > 0)) {
                        chosen = false;
                    }
                }
            }
            if (chosen) {
                String newState = job.getState();
                if (newState.equals("Scheduled")) {
                    scheduledRB++;
                } else {
                    if (newState.equals("Running")) {
                        runningRB++;
                    }
                }
            }
        }
    }

    public Hashtable<String, Job> getGridRBJobs() {
        return new Hashtable<String, Job>(LatestGridRBJobs);  // for now.
        // consider returning the original map.
    }

    public void pushCEJob(Job job) {
        String id = job.getID();
        String rbChoices = RTM.RealTimeMonitor.getRBList();//"all";//(String) overKill.get("rbList");
        String voChoices = RTM.RealTimeMonitor.getVOList();//"all";//(String) overKill.get("voList");
        synchronized (LatestGridCEJobs) {  // LRU cache
            try {
                // ad a Job to the buffer. Dont do stats here
                Job oldJob = LatestGridCEJobs.put(id, job);
                // now limit stats according to choices
                boolean chosen = false;
                if (rbChoices.equals("all")) {
                    chosen = true;
                } else {
                    String thisRB = "-" + oldJob.getRB() + "-";
                    if (rbChoices.indexOf(thisRB) > 0) {
                        chosen = true;
                    }
                }
                if (chosen) {
                    if (!voChoices.equals("all")) {
                        String thisVO = "-" + oldJob.getVO() + "-";
                        if (!(voChoices.indexOf(thisVO) > 0)) {
                            chosen = false;
                        }
                    }
                }

                if (chosen) {
                    String oldState = oldJob.getState();
                    if (oldState.equals("Scheduled")) {
                        //jm scheduledCE--;
                    } else {
                        if (oldState.equals("Running")) {
                            //jm    runningCE--;
                        }
                    }
                }
            } catch (Exception e) {
            } // means there was no old value 

            boolean chosen = false;
            if (rbChoices.equals("all")) {
                chosen = true;
            } else {
                String thisRB = "-" + job.getRB() + "-";
                if (rbChoices.indexOf(thisRB) > 0) {
                    chosen = true;
                }
            }
            if (chosen) {
                if (!voChoices.equals("all")) {
                    String thisVO = "-" + job.getVO() + "-";
                    if (!(voChoices.indexOf(thisVO) > 0)) {
                        chosen = false;
                    }
                }
            }
            if (chosen) {
                String newState = job.getState();
                if (newState.equals("Scheduled")) {
                    //jm scheduledCE++;
                } else {
                    if (newState.equals("Running")) {
                        //jm runningCE++;
                    }
                }
            }
        }
    }

    public Hashtable<String, Job> getGridCEJobs() {
        return new Hashtable<String, Job>(LatestGridCEJobs); // see  getGridRBJobs()
        //return GridCEJobs;
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

    public ArrayList<String> getCEs() {
        return CEs;
    }

    public ArrayList<String> getRBs() {
        return RBs;
    }

    public synchronized int getScheduledCE() {
        return scheduledCE;
    }

    public synchronized int getRunningCE() {
        return runningCE;
    }

    public synchronized int getScheduledRB() {
        return scheduledRB;
    }

    public synchronized int getRunningRB() {
        return runningRB;
    }

    public synchronized JLabel getLogoLabel() {
        return logoLabel;
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

    private void calculateBreaks() {
        if ((scheduledCE + runningCE) > 0) {
            degree_break = (int) (359 * (float) runningCE / (float) (scheduledCE + runningCE));
            ce_scale_factor = Math.log10((double) runningCE);
            ce_scale_factor = (ce_scale_factor > 1) ? ce_scale_factor : 1;
        } else {
            degree_break = -1;
        }
    }
    // need better function?

    private void calculateZoom() {
        //zoom = Math.sqrt(wwd.getCurrentPosition().getElevation())/ 700;
        //zoom = Math.sqrt(wwd.getModel().getGlobe().getElevation(Angle.fromDegrees(latitude), Angle.fromDegrees(longitude))) / 5;
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
     * Update site status to be chosen for display or no, i.e if job at this site
     * contribute to transfers. Running/Scheduled job numbers are updated as well.
     */
    public void reChoose() {

        String rbChoices = RTM.RealTimeMonitor.getRBList();//"all";//(String) overKill.get("rbList");
        String voChoices = RTM.RealTimeMonitor.getVOList();//"all";//(String) overKill.get("voList");

        // thisRBChosen is used for drawing job transfers.
        if (rbChoices.equals("all")) {
            thisRBChosen = true;
        } else {
            boolean newThisRBChosen = false;
            Iterator iterateRBs = RBs.iterator();
            while (iterateRBs.hasNext()) {
                String ip = (String) iterateRBs.next();
                String thisRB = "-" + ip + "-";
                if (rbChoices.indexOf(thisRB) > 0) {
                    newThisRBChosen = true;
                }
            }
            thisRBChosen = newThisRBChosen;
        }



// Statistics is now obtained by a separate thread, not based on Job object contained in
// the LatestGrid**Jobs. The latter only contain the latest jobs to avoid memory leaks.
// RB statistics for the site
        int newScheduledRB = 0;
        int newRunningRB = 0;
// CE statistics for the site
        int newScheduledCE = 0;
        int newRunningCE = 0;

        List<Object> CL = JobStatisticsHandler.getSynchronizedCElist();
                // at startup the stats might not be ready, and CL is null. catch the exception
        // and wait for the next update
        try {
        for (Object o : CL) {
            Ce ce = (Ce) o;
            // does this CE/RB belog to this Site ?
            if (!CEs.contains(ce.getIp()) && !RBs.contains(ce.getRb())) {
                continue;
            }
            boolean chosen = false;
            if (rbChoices.equals("all")) {
                chosen = true;
            } else {
                String thisRB = new String("-" + ce.getRb() + "-");
                if (rbChoices.indexOf(thisRB) > 0) {
                    chosen = true;
                }
            }
            if (chosen) {
                if (!voChoices.equals("all")) {
                    String thisVO = new String("-" + ce.getVo() + "-");
                    if (!(voChoices.indexOf(thisVO) > 0)) {
                        chosen = false;
                    }
                }
            }
            if (chosen) {
                if (CEs.contains(ce.getIp())) {
                    newRunningCE += ((State) (ce.getState())).getRunning();
                    newScheduledCE += ((State) (ce.getState())).getScheduled();
                }
                if (RBs.contains(ce.getRb())) {
                    newRunningRB += ((State) (ce.getState())).getRunning();
                    newScheduledRB += ((State) (ce.getState())).getScheduled();
                }
            }
        }
        } catch (NullPointerException e) {
            Logger.getLogger(RTM.layers.Site.class.getName()).log(Level.FINE," could not update stats, Stats Handler not ready");
        }
        // Panda transfers enabled?
        boolean showp = RTM.RealTimeMonitor.getAf().getWwd().getModel().
                getLayers().getLayerByName("Panda Transfers Layer").isEnabled();
        //gLite transfers enabled ?
        boolean showg = RTM.RealTimeMonitor.getAf().getWwd().getModel().
                getLayers().getLayerByName("Transfers Layer").isEnabled();
        // reset counters.
        scheduledCE = runningCE = 0;
        scheduledRB = runningRB = 0;

        if (showg) {
            scheduledCE = newScheduledCE;
            runningCE = newRunningCE;

            scheduledRB = newScheduledRB;
            runningRB = newRunningRB;
            //System.out.println("glite runningCE"+runningCE);
        }
        if (showp) {
            // if panda site is missing, getXXX would return  -1. so max it.
            scheduledCE += Math.max(0, RTM.datasource.panda.JobStatisticsHandler.getInstance().getScheduled(name));
            runningCE += Math.max(0, RTM.datasource.panda.JobStatisticsHandler.getInstance().getRunning(name));
            //System.out.println("Total " + name + " running CE (panda added) "+runningCE);
        }
        // RB/VO selection, LayerSelectionAction and auto refresh will reChoose()
    }

    /**
     * Draw CE pie-charts ( running and scheduled jobs in different colours) and RB rotating triangles.
     * The actual circle size is defined in Pulser.
     * @param dc
     * @throws java.lang.Exception
     */
    public void render(DrawContext dc) {

        ////O.a("rendering...");

        if (first) {
            addDrawInfo();
            first = false;
        }

        this.calculateZoom();

        if (!(CEs.isEmpty() && RBs.isEmpty())) {
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

                if (!CEs.isEmpty()) {
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
                } else {
                    //O.a("10a");
                }

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
                    gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
                    gl.glBegin(GL2.GL_POLYGON);
                    gl.glVertex3d(x + size * coordinates[pos1][0], y + size * coordinates[pos1][1], z + size * coordinates[pos1][2]);
                    gl.glVertex3d(x + size * coordinates[pos2][0], y + size * coordinates[pos2][1], z + size * coordinates[pos2][2]);
                    gl.glVertex3d(x + size * coordinates[pos3][0], y + size * coordinates[pos3][1], z + size * coordinates[pos3][2]);
                    gl.glEnd();
                    gl.glPopAttrib();
                } else {
                    //O.a("10b");
                }
                gl.glColor4f(1f, 1f, 1f, 1f);
                gl.glPopAttrib();
                gl.glEnable(GL2.GL_DEPTH_TEST);
                gl.glDisable(GL2.GL_LINE_SMOOTH);
                gl.glDisable(GL2.GL_POLYGON_SMOOTH);
            }
        } else {
            //O.a("0");
        }
    }

    public Position getPosition() {
        return new Position(Angle.fromDegrees(latitude), Angle.fromDegrees(longitude), 0.0);
    }

    @Override
    public String toString() {
        String s = "***Site: \n";
        s = s + "Name: " + this.getName() + "\n" + "Position: " + this.getPosition().toString() + state() + "\n***end";
        return s;
    }
    /**
     * Show Site data on a SidePanel. Only if a site belongs to a layer which is enabled.
     */
    public void showPanel() {
        if (isLayerEnabled("Sites Layer")) {
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

    private String state() {
        return isLayerEnabled("Sites Layer") ? " (Enabled) " : " (Disabled) ";
    }

}
