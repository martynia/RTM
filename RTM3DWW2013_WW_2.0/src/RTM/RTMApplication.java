/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM;

import RTM.config.Config;
import RTM.config.Configurator;
import RTM.apple.FullScreen;
import RTM.ui.animations.Animators;
import RTM.ui.sidePanel.SidePanel;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.RenderingExceptionListener;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.util.StatisticsPanel;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.LayerPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * Main program.
 * @author martynia
 */
public class RTMApplication extends ApplicationTemplate {

    protected static class RTMAppFrame extends ApplicationTemplate.AppFrame {

        public RTMAppFrame() {
            super(true, false, false);
        }

        public RTMAppFrame(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel) {
            super(includeStatusBar, includeLayerPanel, includeStatsPanel);
        }

        @Override
        public Dimension getCanvasSize() {
            return canvasSize;
        }

        @Override
        protected void initialize(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel) {

            // Create the WorldWindow.
            canvasSize = new Dimension(1200, 900);
            this.wwjPanel = new ApplicationTemplate.AppPanel(canvasSize, includeStatusBar);
            //this.wwjPanel = this.createAppPanel(this.getCanvasSize(), includeStatusBar);
            //this.wwjPanel.getWwd().setPreferredSize(new Dimension(800, 600));
            this.wwjPanel.setPreferredSize(this.getCanvasSize());

            // Put the pieces together.
            // create the RTM Application desktop with a side panel on the left and the ww panel on the right
            rtmDesktop = new RTMApplicationDesktop(this.getCanvasSize(), this.wwjPanel);
            // add the desktop to the frame
            this.getContentPane().add(rtmDesktop, BorderLayout.CENTER);

            if (includeLayerPanel) {
                System.out.println(" adding layer panel..");
                this.layerPanel = new LayerPanel(this.wwjPanel.getWwd(), null);
                this.getContentPane().add(this.layerPanel, BorderLayout.WEST);
            }

            if (includeStatsPanel || System.getProperty("gov.nasa.worldwind.showStatistics") != null) {
                this.statsPanel = new StatisticsPanel(this.wwjPanel.getWwd(), new Dimension(250, this.getCanvasSize().height));
                this.getContentPane().add(this.statsPanel, BorderLayout.EAST);
            }

            // Create and install the view controls layer and register a controller for it with the World Window.
            ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
            insertBeforeCompass(getWwd(), viewControlsLayer);
            this.getWwd().addSelectListener(new ViewControlsSelectListener(this.getWwd(), viewControlsLayer));

            // Search the layer list for layers that are also select listeners and register them with the World
            // Window. This enables interactive layers to be included without specific knowledge of them here.
            for (Layer layer : this.wwjPanel.getWwd().getModel().getLayers()) {
                if (layer instanceof SelectListener) {
                    this.getWwd().addSelectListener((SelectListener) layer);
                }
            }

            new RealTimeMonitor(rtmDesktop, true);
            // add the menubar
            this.setJMenuBar(RTMMenuBar.getInstance());
            // allow full screen (Mac only, otherwise no effect)
            //RTM.apple.FullScreen.enableFullScreenMode(this);

            if (Configurator.getInstance().wantFullScreen()) {

                FullScreen.init(this);
            }

            this.pack();
            if (System.getProperty("rtm.noAbout") == null) {
                RealTimeMonitor.showAboutIFrame();
            }
            RTMMenuBar.getInstance().customize();
            // animate ?
            if (Configurator.getInstance().isAnimationEnabled()) {
                // wait fot he globe to appear before animating it !
                new Thread() {

                    final BasicOrbitView view = (BasicOrbitView) RealTimeMonitor.getAf().getWwd().getView();

                    @Override
                    public void run() {
                        try {
                            // animations
                            Class.forName("RTM.ui.animations.SpinAnimator");
                            Class.forName("RTM.ui.animations.PathFollowingAnimator");                            
                            
                            while (true) {
                                if (view.getEyePosition() == null) { // view not yet applied !
                                    try {
                                        Thread.sleep(4000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(RTMApplication.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } else {
                                    Animators.getInstance().createAnimator().animate();
                                    break;
                                }
                            }
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(RTMApplication.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.start();
            }
            // Center the application on the screen.
            WWUtil.alignComponent(null, this, AVKey.CENTER);
            this.setResizable(true);
            // frame size when not maximised
            this.setSize(Config.getFrameWidth(), Config.getFrameHeight());
            // frame position
            this.setLocation(Config.getXoffset(), Config.getYoffset());
            // maximise the frame
            setExtendedState(MAXIMIZED_BOTH);
            //
            if (Configurator.getInstance().wantFullScreenAtStart()) {
                FullScreen.toggleFullScreen(this);
            }

        }
        private JSplitPane split;
        private RTMApplicationDesktop rtmDesktop;
        private SidePanel sidePanel;
        private Dimension canvasSize;
    }

    /**
     * Create the RTM desktop. This is a JDesktopPane which holds a JSplitPane. The JSplitPane
     * hosts the RTM globe on the right and a place for site windows (histograms etc.) on the left.
     */
    public static class RTMApplicationDesktop extends JDesktopPane {

        private final JSplitPane split;
        private final SidePanel sidePanel;
        private AppPanel wwjPanel;

        public RTMApplicationDesktop(Dimension size, AppPanel wwjPanel) {
            this.wwjPanel = wwjPanel;
            //
            sidePanel = new SidePanel();
            sidePanel.setPreferredSize(new Dimension((int) 0.4 * size.width, size.height));
            split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, sidePanel, wwjPanel);
            split.setOneTouchExpandable(true);


            setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
            add(split);
            setPreferredSize(size);

            addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(ComponentEvent ev) {
                    Dimension deskSize = getSize();
                    split.setBounds(0, 0, deskSize.width, deskSize.height);
                    sidePanel.getTabPnl().validate();
                    sidePanel.validate();
                    split.validate();
                    validate();
                }
            });
            split.setDividerLocation(0.4);
        }

        //public WorldWindowGLCanvas getWwd() {
        public WorldWindowGLCanvas getWwd() {
            // cast as WW 1.4 
            return (WorldWindowGLCanvas) wwjPanel.getWwd();
        }

        public JSplitPane getSplitPane() {
            return split;
        }

        public SidePanel getSidePanel() {
            return sidePanel;
        }
    }

    public static void main(String[] args) {

        // Call the static start method like this from the main method of your derived class.
        // Substitute your application's name for the first argument.
        ApplicationTemplate.start("RTM World Wind Application", RTMAppFrame.class);

    }
}
