package RTM;

import RTM.config.Config;
import RTM.config.Configurator;
import RTM.datasource.filetransfer.FileTransferSites;
import RTM.datasource.filetransfer.FileTransferUpdatesHandler;
import RTM.datasource.glite.DataPlayback;
import RTM.layers.DigitalClock;
import RTM.datasource.glite.EventHandler;
import RTM.job.GenericJob;
import RTM.datasource.glite.JobStatisticsHandler;
import RTM.datasource.glite.XMLSitesHandler;
import RTM.datasource.glite.dataGetter;
import RTM.datasource.network.NetworkUpdatesHandler;
import RTM.exceptions.BadCoordinatesException;
import RTM.layers.JobCounter;
import RTM.layers.Pulser;
import RTM.layers.RenderableLayerSynch;
import RTM.layers.Site;
import RTM.layers.Transfer;
import RTM.ping.WebserverPing;
import RTM.ui.AboutIFrame;
import RTM.ui.RBControlsIFrame;
import RTM.ui.RTMKeyAdapter;
import RTM.ui.RTMMouseLnr;
import RTM.ui.SitePositionMap;
import RTM.ui.VOControlsIFrame;
import RTM.ui.jobInFrame.JobsIFrame;
import RTM.ui.sidePanel.SidePanel;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.ScreenImage;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JSplitPane;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import RTM.datasource.panda.PandaUpdatesHandler;
import RTM.datasource.reader.Reader;
import RTM.layers.DataTransferSite;
import RTM.layers.DataTransferTrace;
import RTM.layers.NetworkSite;
import RTM.layers.RTMMarkerAttributes;
import RTM.playback.GlitePlaybackList;
import RTM.playback.PlaybackFactory;
import RTM.util.BoundedSortedMap;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.render.markers.MarkerAttributes;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;

/**
 * The RealTimeMonitor application is
 * initialized by performing next steps:
 * 1)Initializing Collections
 * 2)Getting sites
 * 3)Creating pulser (pulser is responsible for sites' circle size)
 * 4)Adding sites to layer
 * 5)Adding transfers to layer
 * 6)Adding layers
 * 7)Creating dataGetter
 * 8)Creating EventHandler
 * 9)Creating redraw thread
 * 10)Starting threads
 *
 * @author Mikhail Khrypach
 * (JM) changed the af type to be JDesktopPane.
 */
public class RealTimeMonitor {

    private static Hashtable<String, Site> Sites;
    private static Hashtable<String, NetworkSite> NetworkSites;
    private static Hashtable<String, DataTransferSite> FileTransferSitesMap;
    private static FileTransferSites ftsDB;
    private static ArrayList<Marker> FileTransferSitesMarkers = new ArrayList<Marker>();
    private static MarkerLayer FTSMarkerLayer = new MarkerLayer();
    private static Hashtable<String, Transfer> Transfers;
    private static Hashtable<String, Transfer> PandaTransfers;
    private static Hashtable<String, Transfer> FileTransfers; // file transfer links (traces)
    private static TreeMap<java.sql.Timestamp, GenericJob> Events;
    private static TreeSet<String> ActiveVOs;
    private static RenderableLayerSynch sitesLayer;
    private static RenderableLayerSynch networkSitesLayer;
    private static RenderableLayerSynch fileTransferSitesLayer;
    private static RenderableLayerSynch transfersLayer;
    private static RenderableLayerSynch pandaTransfersLayer;
    private static RenderableLayerSynch logoLayer;
    private static RenderableLayerSynch digitalClockLayer;
    private static RenderableLayerSynch jobsStatsLayer;    //spm maps position on the globe to a site
    //it is used when selecting site on the globe
    private static SitePositionMap spm;
    //af is the parent frame of the application
    //application starts with creation of this frame
    //private static AppFrame af;
    private static RTMApplication.RTMApplicationDesktop af; // JM modif
    //initialization status information. appears on user's screen
    //when application starts
    private static SplashGreeting splash;
    private static Point inFramesPosition;
    private static JobsIFrame jobsInFrame;
    private static RTMKeyAdapter keyAd;
    private static String rbList;
    private static String voList;
    private static Collection tfsSyncC;  // glite Transfers
    private static Collection ptfsSyncC; // Panda Transfers
    private static Collection fileSyncC; // File Transfers
    private static Collection<Site> stsSyncC; // sites (changed JM 08.2011)
    private static Collection<NetworkSite> nstsSyncC; //
    private static Collection<DataTransferSite> dataSyncC;
    private static RBControlsIFrame rbControlsIFrame;
    private static VOControlsIFrame voControlsIFrame;
    private static AboutIFrame aboutIFrame;
    private static boolean prop;
    //logos
    private static ScreenImage logoImperial;
    private static ScreenImage logoGrid;
    private static ScreenImage logoEgee;
    // time format
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static RenderableLayerSynch fileTransfersLayer;
    private static RenderableLayer ftsAnnotationLayer = new RenderableLayer();
    private static dataGetter dataGetter;
    private static Thread dataGetterThread;
    private static DataPlayback dataPlayback;
    private static Thread gLitePlaybackThread;
    private static PandaUpdatesHandler puHandler;

    public static HashMap<String, String> getReverseFileTransferSites() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static PandaUpdatesHandler getPandaUpdatesHandler() {
        return puHandler;
    }

    private static class ReenderableLayer {

        public ReenderableLayer() {
        }
    }

    public RealTimeMonitor(final RTMApplication.RTMApplicationDesktop af, boolean p) {
        RealTimeMonitor.af = af;
        RealTimeMonitor.prop = p;

        // initialise the splash screen
        splash = SplashGreeting.getInstance();

        //Initializing vars
        Events = new TreeMap<java.sql.Timestamp, GenericJob>();
        Transfers = new Hashtable<String, Transfer>();
        PandaTransfers = new Hashtable<String, Transfer>();
        FileTransfers = new Hashtable<String, Transfer>();
        ActiveVOs = new TreeSet<String>();
        sitesLayer = new RenderableLayerSynch();
        networkSitesLayer = new RenderableLayerSynch();
        fileTransferSitesLayer = new RenderableLayerSynch();
        transfersLayer = new RenderableLayerSynch();
        pandaTransfersLayer = new RenderableLayerSynch();
        fileTransfersLayer = new RenderableLayerSynch();
        logoLayer = new RenderableLayerSynch();
        digitalClockLayer = new RenderableLayerSynch();
        jobsStatsLayer = new RenderableLayerSynch();

        spm = new SitePositionMap();
        logoImperial = new ScreenImage();
        logoGrid = new ScreenImage();
        logoEgee = new ScreenImage();
        rbList = "all";
        voList = "all";

        //Adjust application clock to the server clock
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        O.p("Current time is: "
                + timeFormat.format(Calendar.getInstance().getTimeInMillis())
                + " UTC");
        O.p("Getting sites...");
        //getting sites from server
        // initialise the splash screen
        splash.getProgressBarPanel().updateBar(5, "Getting Sites ... wait ");
        Sites = retrieveSites();
        int numSites = Sites.size();
        System.out.println(
                (new StringBuilder()).append("number of sites = ").append(Sites.size()).toString());
        splash.getProgressBarPanel().updateBar(5, "Getting Sites " + numSites + " ... wait");

        O.p("Creating pulser & adding sites to site-position map...");
        //pulser is responsible for sites' graphical representation circle size
        //pulser runs in a separate thread
        Pulser pulser = new Pulser();
        Collection<Site> isites = Sites.values();
        int startProgress = 5;
        int endProgress = 60;
        int progress = 0;
        int ii = 0;
        for (Site s : isites) {
            pulser.addCS(s.getCS());
            progress = (int) (startProgress + (double) (ii * (endProgress - startProgress)) / numSites);
            splash.getProgressBarPanel().updateBar(progress, "Site:" + s.getName());
            spm.addSite(s);
            ii++;
        }
        Thread pulserThread = new Thread(pulser);

        O.p("Creating IP lookup table for RB,CE->site lookup");
        createIPLookup(isites);
// NEW: network sites
        NetworkSites = retrieveNetworkSites();
        System.out.println(
                (new StringBuilder()).append("number of sites = ").append(NetworkSites.size()).toString());
        O.p("Creating network pulser & adding sites to site-position map...");
        Pulser npulser = new Pulser();
        Collection<NetworkSite> insites = NetworkSites.values();
        for (NetworkSite s : insites) {
            npulser.addCS(s.getCS());
            //progress = (int) (startProgress + (double)(ii*(endProgress-startProgress)) / numSites);
            //splash.getProgressBarPanel().updateBar(progress, "Site:" + s.getName());
            spm.addSite(s);
            ii++;
        }
        Thread npulserThread = new Thread(npulser);
// NEW CMS File transfer sites
        ftsDB = new FileTransferSites();
        FileTransferSitesMap = ftsDB.getSites();
        System.out.println(
                (new StringBuilder()).append("number of FileTransferSites sites = ").append(FileTransferSitesMap.size()).toString());
        System.out.println(FileTransferSitesMap.toString());
        Pulser fpulser = new Pulser();
        Collection<DataTransferSite> ifsites = FileTransferSitesMap.values();
        for (DataTransferSite s : ifsites) {
            fpulser.addCS(s.getCS());
            spm.addSite(s);
            ii++;
        }
        Thread fpulserThread = new Thread(fpulser);
//        
        O.p("Creating Jobs statisctics thread");
        JobStats jobStats = new JobStats(isites);
        Thread statsThread = new Thread(jobStats);

        O.p("Adding sites to layer...");
        //important!!! to use synchronizedCollection. otherwise concurrency
        //problems might accur.
        stsSyncC = Collections.synchronizedCollection((Collection<Site>) isites);
        sitesLayer.setRenderables((Iterable) stsSyncC);

        O.p("Adding network sites to layer...");
        //important!!! to use synchronizedCollection. otherwise concurrency
        //problems might accur.
        nstsSyncC = Collections.synchronizedCollection((Collection<NetworkSite>) insites);
        networkSitesLayer.setRenderables((Iterable) nstsSyncC);

        O.p("Adding FileTransfer sites to layer...");
        //important!!! to use synchronizedCollection. otherwise concurrency
        //problems might accur.
        dataSyncC = Collections.synchronizedCollection((Collection<DataTransferSite>) ifsites);
        fileTransferSitesLayer.setRenderables((Iterable) dataSyncC);

        FTSMarkerLayer.setMarkers(FileTransferSitesMarkers);
        FTSMarkerLayer.setName("FTS Site Markers");
        FTSMarkerLayer.setEnabled(false);


        O.p("Adding transfers to layer...");
        //important!!! to use synchronizedCollection. otherwise concurrency
        //problems might accur.
        tfsSyncC = Collections.synchronizedCollection(Transfers.values());
        transfersLayer.setRenderables((Iterable) tfsSyncC);

        ptfsSyncC = Collections.synchronizedCollection(PandaTransfers.values());
        pandaTransfersLayer.setRenderables((Iterable) ptfsSyncC);

        fileSyncC = Collections.synchronizedCollection(FileTransfers.values());
        fileTransfersLayer.setRenderables((Iterable) fileSyncC);

        O.p("Adding logos to layer...");
        Image imImperial = null;
        Image imGrid = null;
        Image imEgee = null;
        try {
            imImperial = ImageIO.read(this.getClass().getResourceAsStream("Imperial_logo_trans_small.png"));
            imGrid = ImageIO.read(this.getClass().getResourceAsStream("GridPP_logo_trans.png"));
            //imEgee = ImageIO.read(this.getClass().getResourceAsStream("egee_logo_trans.png"));
            imEgee = ImageIO.read(this.getClass().getResourceAsStream("EGI-Logo-small-trans.png"));
        } catch (IOException ex) {
            Logger.getLogger(RealTimeMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        logoImperial.setImageSource(imImperial);
        logoGrid.setImageSource(imGrid);
        logoEgee.setImageSource(imEgee);
        logoLayer.addRenderable(logoGrid);
        logoLayer.addRenderable(logoImperial);
        logoLayer.addRenderable(logoEgee);
        //positionLogo(); getAf().getWwd().getWidth() and getHight() give 0 here, no point to position logos at
        // this stage
        getAf().getWwd().addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent ev) {
                positionLogo();
            }

            @Override
            public void componentShown(ComponentEvent ev) {
                //System.out.println("Component shown !");
                positionLogo();
            }
        });
        O.p(" Adding digital clock to digitalClockLayer ...");
        DigitalClock digitalClock = new DigitalClock("", new Point(10, 10));
        digitalClockLayer.addRenderable(digitalClock);
        O.p("Adding Job Counters Layer");
        JobCounter runningJobsCounter = new JobCounter("", new Point(260, 10));
        jobStats.addObserver(runningJobsCounter);
        //JobCounter scheduledJobsCounter = new JobCounter("Scheduled: ", new Point(300, 10));
        //jobStats.addObserver(scheduledJobsCounter);
        // add a renderable to the layer
        jobsStatsLayer.addRenderable(runningJobsCounter);
        O.p("Adding layers...");
        //do not need built in picking as we are using our own site selection
        //technique.
        sitesLayer.setPickEnabled(false);
        networkSitesLayer.setPickEnabled(false);
        fileTransferSitesLayer.setPickEnabled(false);
        fileTransfersLayer.setPickEnabled(false);
        transfersLayer.setPickEnabled(false);
        pandaTransfersLayer.setPickEnabled(false);
        logoLayer.setPickEnabled(false);
        digitalClockLayer.setPickEnabled(false);
        jobsStatsLayer.setPickEnabled(false);
        // Layer anabling in RTMMenuBar class, by clicking, so put sitesLayer and transfersLayer to false here.
        sitesLayer.setEnabled(false);
        transfersLayer.setEnabled(false);
        // 2 lines above were set to true before.
        networkSitesLayer.setEnabled(false);
        fileTransferSitesLayer.setEnabled(false);
        fileTransfersLayer.setEnabled(false);
        ftsAnnotationLayer.setEnabled(false);
        pandaTransfersLayer.setEnabled(false);
        logoLayer.setEnabled(true);
        digitalClockLayer.setEnabled(true);
        if (System.getProperty("rtm.noStats") == null) {
            jobsStatsLayer.setEnabled(true);
        } else {
            jobsStatsLayer.setEnabled(false);
            //
        }
        sitesLayer.setName("Sites Layer");
        networkSitesLayer.setName("Network Sites Layer");
        fileTransferSitesLayer.setName("FTS Layer");
        fileTransfersLayer.setName("FTS Transfer Layer");
        transfersLayer.setName("Transfers Layer");
        pandaTransfersLayer.setName("Panda Transfers Layer");
        logoLayer.setName("Logo Layer");
        digitalClockLayer.setName("Clock Layer");
        jobsStatsLayer.setName("Job Stats Layer");
        ftsAnnotationLayer.setName("FTS Annotations");
        sitesLayer.setOpacity(0);
        getAf().getWwd().getModel().getLayers().add(sitesLayer);
        getAf().getWwd().getModel().getLayers().add(networkSitesLayer);
        getAf().getWwd().getModel().getLayers().add(transfersLayer);
        getAf().getWwd().getModel().getLayers().add(pandaTransfersLayer);
        getAf().getWwd().getModel().getLayers().add(logoLayer);
        getAf().getWwd().getModel().getLayers().add(digitalClockLayer);
        getAf().getWwd().getModel().getLayers().add(jobsStatsLayer);
        getAf().getWwd().getModel().getLayers().add(fileTransferSitesLayer);
        getAf().getWwd().getModel().getLayers().add(fileTransfersLayer);
        getAf().getWwd().getModel().getLayers().add(FTSMarkerLayer);
        getAf().getWwd().getModel().getLayers().add(ftsAnnotationLayer);
        addFTSMarkerListener();
        RenderableLayer cylinder = new RenderableLayer();
        cylinder.setName("FTS Disks");
        cylinder.setEnabled(false); // would not show anyway if the main FTS layer disabled.
        getAf().getWwd().getModel().getLayers().add(cylinder);

        //currently is used to disable layers that created by default
        //in ApplicationTemplate
        disableLayers();
        // define which layers are enabled/disabled based on system properties
        // if no system properties are set, accept the default defined above
        // done by menus now: customizeLayers();
        //this.getLayerPanel().update(this.getWwd());

        // inpired by Science Museum requirements
        // There is no MenuBar defined yet. The RTMMenuBar enables RTM layers and feeds
        // Get layer configuration from the steering file, so we might affect
        // the defaults set above. Get all layers mentioned in the config file and
        // enable/disable them

        configureLayers();

        //
        O.p("Adding input controls...");
        getAf().getWwd().addMouseListener(new RTMMouseLnr());
        getAf().getWwd().addKeyListener(getKeyAdapter());
        //
        O.p(" Setting the initial eye point (zoom in)");
        setInitialGlobeView(); //
        O.p("Creating dataGetter...");
        splash.getProgressBarPanel().updateBar(60, "Starting data handler");
        //dataGetter is used to get real-time gliteJobs
        dataGetter = new dataGetter();
        dataGetterThread = new Thread(dataGetter);
        dataPlayback = new DataPlayback();
        new RTM.datasource.panda.DataPlayback(); // just load a class for a factory 
        gLitePlaybackThread = new Thread(dataPlayback);

        // start Network data collection
        NetworkUpdatesHandler nah = new NetworkUpdatesHandler(60, 0);
        // Starting FTS udata handler
        FileTransferUpdatesHandler fts = new FileTransferUpdatesHandler(1000); // 1 second delay
        // starting PandaUpdatesHandler
        O.p("Creating PandaUpdatesHandler...");
        splash.getProgressBarPanel().updateBar(75, "Starting PandaUpdates");
        puHandler = new PandaUpdatesHandler(600, 0);
        O.p("Creating EventHandler...");
        //event handler controls transfers
        EventHandler eventhandler = new EventHandler();
        Thread ehThread = new Thread(eventhandler);
        //
        DataTransferTrace ftsTrace = new DataTransferTrace(5, 5); // every 5 sec, afer 5 sec initial delay
        O.p("Creating redraw thread...");
        //redraw forces glcanvas to be rendered every N milisecons
        // N is specified in the redraw class defn.
        ArrayList<Collection> cList = new ArrayList<Collection>();
        cList.add(RealTimeMonitor.getTfsSyncCollection());
        cList.add(RealTimeMonitor.getPtfsSyncCollection());
        cList.add(RealTimeMonitor.getFileSyncCollection());

        Thread redrawThread = new Thread(new Redraw(cList));
        //Thread predrawThread = new Thread(new Redraw(RealTimeMonitor.getPtfsSyncCollection()));  // Panda Transfers
        //Thread ftsredrawThread = new Thread(new Redraw(RealTimeMonitor.getFileSyncCollection()));
        O.p("Creating statistics thread");
        Thread statThread = new Thread(new JobStatisticsHandler());

        O.p("Create ping thread");
        Thread pingThread = new Thread(new WebserverPing());
        O.p("Starting threads...");
        pulserThread.start();
        npulserThread.start();
        fpulserThread.start();
        dataGetterThread.start();
        ehThread.start();
        redrawThread.start();
        //predrawThread.start();
        statsThread.start();
        statThread.start();
        pingThread.start();
        splash.dispose(0);
        splash = null;
        // Monitor task
        new Monitor(30, 60);
        new ServerMonitor(30,0,5000,5000);
    }

    public static void setDataGetter(dataGetter dataGetter) {
        RealTimeMonitor.dataGetter = dataGetter;
    }

    public static void setDataGetterThread(Thread dataGetterThread) {
        RealTimeMonitor.dataGetterThread = dataGetterThread;
    }

    public static void setDataPlayback(DataPlayback dataPlayback) {
        RealTimeMonitor.dataPlayback = dataPlayback;
    }

    public static void setgLitePlaybackThread(Thread gLitePlaybackThread) {
        RealTimeMonitor.gLitePlaybackThread = gLitePlaybackThread;
    }

    public static Thread getDataGetterThread() {
        return dataGetterThread;
    }

    public static Thread getgLitePlaybackThread() {
        return gLitePlaybackThread;
    }

    public static dataGetter getDataGetter() {
        return dataGetter;
    }

    public static DataPlayback getDataPlayback() {
        return dataPlayback;
    }
    //gets a table of sites

    private Hashtable<String, Site> retrieveSites() {

        URL dataURL;
        InputStream xmlStream = null;
        InputSource source;
        XMLReader parser;
        XMLSitesHandler handler = null;
        URLConnection dataURLConnection;

        String datString = new String(Config.getWS_URL() + "/dynamic_information/egee-locations.xml");
        datString = System.getProperty("rtm.map_xml_uri", datString);

        try {
            dataURL = new URL(datString);

            dataURLConnection = dataURL.openConnection();
            if (System.getProperty("rtm.http.proxy") != null) {
                dataURLConnection.setRequestProperty("Proxy-Authorization",
                        System.getProperty("rtm.http.proxy"));
            }
            xmlStream = dataURLConnection.getInputStream();
            source = new InputSource(xmlStream);

            parser = XMLReaderFactory.createXMLReader();
            handler = new XMLSitesHandler();

            parser.setContentHandler(handler);
            parser.setErrorHandler(handler);

            parser.parse(source);

            return handler.getSites();

        } catch (MalformedURLException mue) {
            System.out.println("URL problem");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                xmlStream.close();
            } catch (IOException ex) {
                Logger.getLogger(RealTimeMonitor.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        //in case there is an exception return an empty hashtable
        return new Hashtable<String, Site>();
    }

    //disable layers that are not needed when application starts
    private void disableLayers() {
        for (Layer l : getAf().getWwd().getModel().getLayers()) {
            if (l.getName().equals("Stars")) {
                l.setEnabled(false);
            } else if (l.getName().equals("Atmosphere")) {
                l.setEnabled(false);
            } else if (l.getName().equals("NASA Blue Marble Image")) {
                l.setEnabled(false);
            } else if (l.getName().equals("i-cubed Landsat")) {
                l.setEnabled(false);
            } else if (l.getName().equals("USGS Urban Area Ortho")) {
                l.setEnabled(false);
            } else if (l.getName().equals("USGS Urban Area Ortho")) {
                l.setEnabled(false);
            } else if (l.getName().equals("USDA NAIP")) {
                l.setEnabled(false);
            }
        }
    }

    /**
     * Enable/disable layers by means of the Configurator.
     */
    private void configureLayers() {
        HashMap<String, Boolean> configLayers = Configurator.getInstance().getValidLayers();
        Logger.getLogger(RealTimeMonitor.class.getName()).log(Level.CONFIG, "========= Layer Configurations ==========");
        Logger.getLogger(RealTimeMonitor.class.getName()).log(Level.CONFIG, " #Layers found: {0}", configLayers.size());
        Logger.getLogger(RealTimeMonitor.class.getName()).log(Level.CONFIG, " Layers found: {0}", configLayers);
        Layer l;
        for (String key : configLayers.keySet()) {
            l = RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName(key);
            l.setEnabled(configLayers.get(key).booleanValue());
        }
    }

    public static SitePositionMap getSPM() {
        return spm;
    }

    public static RTMApplication.RTMApplicationDesktop getAf() {
        return RealTimeMonitor.af;
    }

    public static SidePanel getSidePanel() {
        return RealTimeMonitor.getAf().getSidePanel();
    }

    public static void showJobsFrame(Site site, String type, String ip) {

        if (useInFrames()) {
            if (jobsInFrame == null) {
                jobsInFrame = new JobsIFrame();

            }
            jobsInFrame.setJobs(site, type, ip);
            jobsInFrame.requestFocusInWindow();
        } else {
            getSidePanel().showJobsPanel(site, type, ip);
        }
    }

    public static Point getInframePosition() {
        int step = 50;

        if (inFramesPosition == null) {
            inFramesPosition = new Point(10, 10);
            //jm      } else if (((JDesktopPane) getAf().getContentPane()).getAllFrames().length != 0) {
        } else if (getAf().getAllFrames().length != 0) {
            if (getAf().getSize().height / 3 * 2 > step + inFramesPosition.y) {
                inFramesPosition.y = inFramesPosition.y + step;
                if (getAf().getSize().width / 3 * 2 > step + inFramesPosition.x) {
                    inFramesPosition.x = inFramesPosition.x + step;
                }
            }
        } else {
            inFramesPosition.move(10, 10);
        }
        return new Point(inFramesPosition);
    }

    public static void nullJobsFrame() {
        RealTimeMonitor.jobsInFrame = null;
    }

    public static KeyAdapter getKeyAdapter() {
        if (keyAd == null) {
            keyAd = new RTMKeyAdapter();
        }
        return keyAd;
    }

    public static String getRBList() {
        return RealTimeMonitor.rbList;
    }

    public static String getVOList() {
        return RealTimeMonitor.voList;
    }

    public static void setRBList(String rbList) {
        RealTimeMonitor.rbList = rbList;
    }

    public static void setVOList(String voList) {
        RealTimeMonitor.voList = voList;
    }

    public static Collection getTfsSyncCollection() {
        return RealTimeMonitor.tfsSyncC;
    }

    public static Collection getPtfsSyncCollection() {
        return RealTimeMonitor.ptfsSyncC;
    }

    public static Collection getFileSyncCollection() {
        return RealTimeMonitor.fileSyncC;
    }

    public static void nullRBControlsIFrame() {
        RealTimeMonitor.rbControlsIFrame = null;
    }

    public static void reChooseSites() {
        for (Site s : (Collection<Site>) RealTimeMonitor.stsSyncC) {
            s.reChoose();
        }
    }

    public static void showRBControlsIFrame() {
        if (useInFrames()) {
            if (rbControlsIFrame == null) {
                rbControlsIFrame = new RBControlsIFrame();
                rbControlsIFrame.addKeyListener(keyAd);
                rbControlsIFrame.requestFocusInWindow();
            }
        } else {
            getSidePanel().showRBControlsPanel();
        }
    }

    public static void nullVOControlsIFrame() {
        RealTimeMonitor.voControlsIFrame = null;
    }

    public static void showVOControlsIFrame() {
        if (useInFrames()) {
            if (voControlsIFrame == null) {
                voControlsIFrame = new VOControlsIFrame();
                voControlsIFrame.addKeyListener(keyAd);
                voControlsIFrame.requestFocusInWindow();
            }
        } else {
            getSidePanel().showVOControlsPanel();
        }
    }

    public static void showAboutIFrame() {
        if (useInFrames()) {
            aboutIFrame = new AboutIFrame();
        } else {
            getSidePanel().showAboutPanel();
        }
    }

    public static void showHelpIFrame() {
        getSidePanel().showHelpPanel();
    }

    public static void nullAboutIFrame() {
        aboutIFrame = null;
    }

    public static boolean useInFrames() {
        return !prop;
    }

    public static Hashtable<String, NetworkSite> getNetworkSites() {
        return RealTimeMonitor.NetworkSites;
    }

    public static Hashtable<String, DataTransferSite> getFileTransferSites() {
        return RealTimeMonitor.FileTransferSitesMap;
    }

    public static ArrayList<Marker> getFileTransferSitesMarkers() {
        return RealTimeMonitor.FileTransferSitesMarkers;
    }

    public static RenderableLayer getFTSAnnotationLayer() {
        return RealTimeMonitor.ftsAnnotationLayer;
    }

    public static Hashtable<String, Site> getSites() {
        return RealTimeMonitor.Sites;
    }

    public static Hashtable<String, Transfer> getTransfers() {
        return RealTimeMonitor.Transfers;
    }

    public static Hashtable<String, Transfer> getPandaTransfers() {
        return RealTimeMonitor.PandaTransfers;
    }

    public static Hashtable<String, Transfer> getFileTransfers() {
        return RealTimeMonitor.FileTransfers;
    }

    public static FileTransferSites getFileTransferSitesDB() {
        return ftsDB;
    }

    public static TreeMap<java.sql.Timestamp, GenericJob> getEvents() {
        return RealTimeMonitor.Events;
    }

    public static TreeSet<String> getActiveVOs() {
        return RealTimeMonitor.ActiveVOs;
    }

    public static JSplitPane getSplit() {
        return RealTimeMonitor.getAf().getSplitPane();
    }

    public static void toggleJobStats() {
        jobsStatsLayer.setEnabled(!jobsStatsLayer.isEnabled());
    }

    public static void enableJobStats(boolean flag) {
        jobsStatsLayer.setEnabled(flag);
    }

    private static void positionLogo() {
        // screen image widh/height can be zero initially... FIXME
        int wh = getAf().getWwd().getWidth();
        int ht = getAf().getWwd().getHeight();
        // use awt image dimensions if screen image dimenstions are 0. Not pretty ...
        //Point pnt = new Point((int) (logoGrid.getImageWidth() / 2 + wh * 0.05), (int) (ht * 0.95 -25 - logoGrid.getImageHeight() / 2));
        Point pnt = new Point((int) (getOptimalImageSize(logoGrid).width / 2 + wh * 0.05), (int) (ht * 0.95 - 25 - getOptimalImageSize(logoGrid).height / 2));
        logoGrid.setScreenLocation(pnt);
        //System.out.println("logoGrid pos " + pnt.x + " " + pnt.y + " logoGrid width: " + logoGrid.getImageWidth());
        //pnt = new Point((int) (logoImperial.getImageWidth() / 2 + wh * 0.05), (int) (ht * 0.95 - logoGrid.getImageHeight() - 50 - logoImperial.getImageHeight() / 2));
        pnt = new Point((int) (getOptimalImageSize(logoImperial).width / 2 + wh * 0.05), (int) (ht * 0.95 - getOptimalImageSize(logoGrid).height - 50 - getOptimalImageSize(logoImperial).height / 2));
        logoImperial.setScreenLocation(pnt);
        //pnt = new Point((int) (logoEgee.getImageWidth() / 2 + wh * 0.05), (int) (ht * 0.95 - logoGrid.getImageHeight() - logoImperial.getImageHeight() - 100 - logoEgee.getImageHeight() / 2));
        pnt = new Point((int) (getOptimalImageSize(logoEgee).width / 2 + wh * 0.05), (int) (ht * 0.95 - getOptimalImageSize(logoGrid).height - getOptimalImageSize(logoImperial).height - 100 - getOptimalImageSize(logoEgee).height / 2));
        //System.out.println("logoEgee pos " + pnt.x + " " + pnt.y);
        logoEgee.setScreenLocation(pnt);

    }

    private static Dimension getOptimalImageSize(ScreenImage screenImage) { // should be inner class fix later
        // if screen size image size is 0 use awt image dimensions instead.
        Dimension dim = null;
        BufferedImage awtImg = null;
        //if (screenImage.getImageHeight() > 0 && screenImage.getImageWidth() > 0) {
        //  dim = new Dimension(screenImage.getImageWidth(), screenImage.getImageHeight());
        //} else {
        try {
            awtImg = (BufferedImage) screenImage.getImageSource();
            dim = new Dimension(awtImg.getWidth(), awtImg.getHeight());
        } catch (Exception ex) {
            dim = new Dimension(0, 0);
        }
        //}
        return dim;
    }

    /**
     * Get Latitude/Longitude values form System properties. Create a Sector object to be used
     * with <code>zoomToSector</code>
     */
    private void setInitialGlobeView() {
        if (System.getProperty("rtm.minLon") != null
                && System.getProperty("rtm.maxLon") != null
                && System.getProperty("rtm.minLat") != null
                && System.getProperty("rtm.maxLat") != null) {
            try {
                double minLon = new Double(System.getProperty("rtm.minLon")).doubleValue();
                double maxLon = new Double(System.getProperty("rtm.maxLon")).doubleValue();
                double minLat = new Double(System.getProperty("rtm.minLat")).doubleValue();
                double maxLat = new Double(System.getProperty("rtm.maxLat")).doubleValue();
                if (minLat >= maxLat || minLon >= maxLon) {
                    throw new BadCoordinatesException(" upper limit lower than lower limit!");
                }
                if (minLat < -90. || maxLat > 90. || minLon < -180. || maxLon > 180.) {
                    throw new BadCoordinatesException(" Coordinated out of range - ignored");
                }
                if (Math.abs(maxLon - minLon) < 5 || Math.abs(maxLat - minLat) < 5.) {
                    throw new BadCoordinatesException(" zoomed too close, use 5 degrees minimum !");
                }
                Sector sector = new Sector(
                        Angle.fromDegrees(minLat),
                        Angle.fromDegrees(maxLat),
                        Angle.fromDegrees(minLon),
                        Angle.fromDegrees(maxLon));
                zoomToSector(getAf().getWwd(), sector);
            } catch (BadCoordinatesException ex) {
                Logger.getLogger(RealTimeMonitor.class.getName()).log(Level.WARNING, " zoom ignored! ", ex);
            } catch (NumberFormatException npe) {
                Logger.getLogger(RealTimeMonitor.class.getName()).log(Level.SEVERE,
                        "One of the values specified is not a floating point number", npe);
            }
        }
        return;
    }

    /**
     * Sets the eye position to make a predefined sector visible. Code posted
     * originally to the WorldWind java forum by remleduff.
     * @param worldWindow
     * @param sector
     */
    private void zoomToSector(WorldWindow worldWindow, Sector sector) {
        double delta_x = sector.getDeltaLonRadians();
        double delta_y = sector.getDeltaLatRadians();

        double earthRadius = worldWindow.getModel().getGlobe().getRadius();

        double horizDistance = earthRadius * delta_x;
        double vertDistance = earthRadius * delta_y;

        // Form a triangle consisting of the longest distance on the ground and the ray from the eye to the center point 
        // The ray from the eye to the midpoint on the ground bisects the FOV
        double distance = Math.max(horizDistance, vertDistance) / 2;
        double altitude = distance / Math.tan(worldWindow.getView().getFieldOfView().radians / 2);

        LatLon latlon = sector.getCentroid();
        Position pos = new Position(latlon, altitude);

        View view = worldWindow.getView();
        view.setEyePosition(pos);

        // TODO: Why doesn't wwj fire this on its own?
        view.firePropertyChange(AVKey.VIEW, null, view);
    }

    private static void createIPLookup(Iterable<Site> ites) {

        for (Site s : ites) {

            String id = s.getID();

            ArrayList<String> CEs = s.getCEs();
            for (String ip : CEs) {
                ipLookup.put(ip, id);
            }

            ArrayList<String> RBs = s.getRBs();
            for (String ip : RBs) {
                ipLookup.put(ip, id);
            }
        }
    }

    public static Hashtable<String, String> getIPLookup() {
        return ipLookup;
    }
    private static Hashtable<String, String> ipLookup = new Hashtable<String, String>();

    private Hashtable<String, NetworkSite> retrieveNetworkSites() {

        Reader reader = new Reader(Config.getWS_URL() + "/dynamic_information/network_sites.xml");
        RTM.datasource.network.XMLSitesHandler netSiteHandler = new RTM.datasource.network.XMLSitesHandler();
        reader.read(netSiteHandler);
        return netSiteHandler.getSites();
    }

    /**
     * Redefine a default layer selection only if a relevant property is present.
     * Obsolete. Now we customeize feeds by means of MenuBar doClick()
     */
    private void customizeLayers() {
        for (Layer l : RealTimeMonitor.getAf().getWwd().getModel().getLayers()) {

            String value = System.getProperty(l.getName());

            if (value != null) {
                if (value.equalsIgnoreCase("true")) {
                    l.setEnabled(true);
                } else if (value.equalsIgnoreCase("false")) {
                    l.setEnabled(false);
                }
            }
            //System.out.println(" Layer "+l.getName()+" Property "+value +" Enebled ? " + l.isEnabled());
        }
    }

    private void addFTSMarkerListener() {
        // react on all Markers, (FIXME)
        RTM.RealTimeMonitor.getAf().getWwd().addSelectListener(new SelectListener() {

            public void selected(SelectEvent event) {
                AnnotationAttributes ftsAnnAttr = new AnnotationAttributes();
                ftsAnnAttr.setVisible(false);

                if (lastHighlit != null
                        && (event.getTopObject() == null || !event.getTopObject().equals(lastHighlit))) {
                    lastHighlit.setAttributes(lastAttrs);
                    RTM.RealTimeMonitor.getFTSAnnotationLayer().removeRenderable(annotation);
                    lastHighlit = null;
                }

                if (!event.getEventAction().equals(SelectEvent.ROLLOVER)) {
                    return;
                }

                if (event.getTopObject() == null || event.getTopPickedObject().getParentLayer() == null) {
                    return;
                }

                if (event.getTopPickedObject().getParentLayer() != FTSMarkerLayer) {
                    return;
                }

                if (lastHighlit == null && event.getTopObject() instanceof Marker) {
                    lastHighlit = (Marker) event.getTopObject();
                    lastAttrs = (RTMMarkerAttributes) lastHighlit.getAttributes();
                    MarkerAttributes highliteAttrs = new RTMMarkerAttributes(lastAttrs);
                    highliteAttrs.setMaterial(Material.WHITE);
                    highliteAttrs.setOpacity(1d);
                    highliteAttrs.setMarkerPixels(lastAttrs.getMarkerPixels() * 1.4);
                    highliteAttrs.setMinMarkerSize(lastAttrs.getMinMarkerSize() * 1.4);
                    lastHighlit.setAttributes(highliteAttrs);
                    // display a linked annotation
                    DataTransferSite linkedSite = (DataTransferSite) lastAttrs.getCustomAttribute();

                    annotation = new GlobeAnnotation("Site: " + linkedSite.getName() + "\n" + linkedSite.getRates().toString(),
                            Position.fromDegrees(linkedSite.getLatitude(), linkedSite.getLongitude(), 0), ftsAnnAttr);
                    RTM.RealTimeMonitor.getFTSAnnotationLayer().addRenderable(annotation);
                }
            }
        });
    }
    private Marker lastHighlit;
    private GlobeAnnotation annotation;
    private RTMMarkerAttributes lastAttrs;
}

class Monitor {

    private class MonitorTask extends TimerTask {

        private int pandaTransferCollSize, pandaTransferSize;
        private int gliteTransferCollSize;
        private int gliteTransferSize;

        public MonitorTask() {
        }

        @Override
        public void run() {

            synchronized (RealTimeMonitor.getPtfsSyncCollection()) {
                pandaTransferCollSize = RealTimeMonitor.getPtfsSyncCollection().size();
                pandaTransferSize = RealTimeMonitor.getPandaTransfers().size();
            }
            synchronized (RealTimeMonitor.getTfsSyncCollection()) {
                gliteTransferCollSize = RealTimeMonitor.getTfsSyncCollection().size();
                gliteTransferSize = RealTimeMonitor.getTransfers().size();
            }
            System.out.print(" ---->>> Panda synchColl size: " + pandaTransferCollSize + " pandaTransferSize " + pandaTransferSize);
            System.out.println(" ---->>> gLite synchColl size: " + gliteTransferCollSize + " gLiteTransferSize " + gliteTransferSize);
        }
    }
    private Timer timer;

    public Monitor(int seconds, int delay) {

        timer = new Timer();
        timer.scheduleAtFixedRate(new MonitorTask(), delay, //initial delay
                seconds * 1000); //subsequent rate
        System.out.println("Monitor task scheduled ...");
    }
}

class ServerMonitor {

    private class ServerMonitorTask extends TimerTask {
        private ServerMonitorTask() {
            
        }
        @Override
        public void run() {
            mean = data.getMean();
            Logger.getLogger(ServerPingTask.class.getName()).log(Level.INFO, " Round trip mean value : {0}", mean);
        }
        private double mean=0, sigma=0;
    }
    private class ServerPingTask extends TimerTask {

        private ServerPingTask(int conn_timeout, int read_timeout) {
            ct=conn_timeout;
            rt=read_timeout;
        }

        @Override
        public void run() {
            try {
                // http://rtm.hep.ph.ic.ac.uk/cgi-bin/epoch_time.cgi
                URL reply = new URL(Config.getWS_URL() + "/cgi-bin/epoch_time.cgi");
                URLConnection epochConnection = reply.openConnection();
                epochConnection.setConnectTimeout(ct);
                epochConnection.setReadTimeout(rt);
                long start = System.currentTimeMillis();
                epochConnection.connect();
                BufferedReader buff = new BufferedReader(new InputStreamReader(epochConnection.getInputStream()));
                String replyString = buff.readLine();
                long stop = System.currentTimeMillis();
                long roundTrip = stop - start;
                Logger.getLogger(ServerPingTask.class.getName()).log(Level.INFO, " Webserver replied{0} round trip: {1} ms"
                        + "", new Object[]{replyString, roundTrip});
                data.put(start, roundTrip);
            } catch (SocketTimeoutException ex) {
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(ServerMonitor.class.getName()).log(Level.SEVERE, "RTM server epoch_time.cgi URL invalid ?", ex);

            } catch (IOException ex) {
                Logger.getLogger(ServerMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int ct=0, rt=0;
    }
    private class ServerMonitorData {
        public ServerMonitorData(int buflen) {
          len=buflen;
          buf = new BoundedSortedMap<Long, Long>(len);
        } 
        public ServerMonitorData() {
            this(50);
        } 
        /**
         * Calculate the mean value of all value elements in the map
         * @return the mean
         */
        public double getMean(){
            double mean = 0;
              // calculate the mean, sigma etc.
            int l = buf.size();
            sums();
            if(l!=0) mean=1.0*sum/l;
            return mean;
        }
        private void sums() {
            long elem = 0;
            synchronized(this) {
               Collection<Long> s = buf.values(); 
               Iterator<Long> i = s.iterator(); // Must be in synchronized block
               while (i.hasNext())
                  elem = i.next().longValue(); 
                  sum += elem;
                  sum2+= elem*elem;
            }
        }
          
        private synchronized void put(long timestamp, long trip) {
            buf.put(Long.valueOf(timestamp), Long.valueOf(trip)); 
        }
        private int len = 50;
        private long sum=0, sum2=0;
        private BoundedSortedMap<Long, Long> buf = null;
    }
     /**
     * 
     * @param seconds ServerPingTask timer rate in seconds
     * @param delay initial ServerPingTask delay in seconds
     * @param conn_timeout URL connection timeout in miliseconds
     * @param read_timeout URL read timeout in miliseconds
     */
    public ServerMonitor(int seconds, int pingDelay, int conn_timeout, int read_timeout) {
        data =  new ServerMonitorData();
        timer = new Timer();
        timer.scheduleAtFixedRate(new ServerPingTask(conn_timeout, read_timeout), pingDelay * 1000, //initial delay
                seconds * 1000); //subsequent rate
        Logger.getLogger(ServerMonitor.class.getName()).log(Level.INFO, "Server Ping task scheduled. Will collect round trip ping times");
        timer2 = new Timer();
        int delay = (int) (pingDelay + 3.5*seconds) ; // calculate the mean from at least 3 pings.
        timer2.scheduleAtFixedRate(new ServerMonitorTask(), delay * 1000, //initial delay
                4 * seconds * 1000); //subsequent rate
        Logger.getLogger(ServerMonitor.class.getName()).log(Level.INFO, "Server Monitor task scheduled. Will analyse ping data");
    }
    private Timer timer, timer2;
    private ServerMonitorData data;
}
