/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM;

import RTM.ui.sidePanel.SidePanel;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.RenderingExceptionListener;
import gov.nasa.worldwind.examples.ClickAndGoSelectListener;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.WorldMapLayer;
import gov.nasa.worldwind.util.StatusBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JDesktopPane;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * This is the main RealTimeMonitor Desktop. It serves as a JContentPane both to applications (AppFrame class) and
 * applets (RTMApplet class).
 * 
 * @author Janusz Martyniak, based on Misha's AppFrame JFrame design
 */
public class RTMApplicationDesktop extends JDesktopPane {

    private static final long serialVersionUID = 426204280515L;
    private Dimension canvasSize = new Dimension(800, 600);
    protected AppPanel wwjPanel;
    private SidePanel sidePnl;
    private JSplitPane split;
    private boolean useSidePanel = true;

    RTMApplicationDesktop() {
        super();
        setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        //
        this.wwjPanel = this.createAppPanel(this.canvasSize, true);
        this.wwjPanel.setPreferredSize(canvasSize);

        // Put the pieces together.
        if (useSidePanel) {
            sidePnl = new SidePanel();
            //split is a JSplitPane. Used for control & interactions with when
            //side panel is active -- it provides a divider which can be dragged
            //in order to change sizes of side panel & AppPanel(Globe).
            split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, sidePnl, this.getWwjPanel());
            split.setOneTouchExpandable(true);

            add(split);
            this.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(ComponentEvent ev) {
                    Dimension deskSize = RTMApplicationDesktop.this.getSize();
                    split.setBounds(0, 0, deskSize.width, deskSize.height);
                    sidePnl.getTabPnl().validate();
                    sidePnl.validate();
                    split.validate();
                }
            });

            split.setDividerLocation(0.4);
        } else {
            add(wwjPanel, JLayeredPane.DEFAULT_LAYER);
        }

        this.wwjPanel.getWwd().addRenderingExceptionListener(new RenderingExceptionListener() {

            public void exceptionThrown(Throwable t) {
                if (t instanceof WWAbsentRequirementException) {
                    String message = "Computer does not meet minimum graphics requirements.\n";
                    message += "Please install up-to-date graphics driver and try again.\n";
                    message += "Reason: " + t.getMessage() + "\n";
                    message += "This program will end when you press OK.";

                    JOptionPane.showMessageDialog(RTMApplicationDesktop.this, message, "Unable to Start Program",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                }
            }
        });

        //RTM is a 'static' class. It has got controls over the application
        //new RealTimeMonitor(this, useSidePanel);
    }

    protected AppPanel createAppPanel(Dimension canvasSize, boolean includeStatusBar) {
        return new AppPanel(canvasSize, includeStatusBar);
    }

    public SidePanel getSidePanel() {
        return sidePnl;
    }

    public JSplitPane getSplitPane() {
        return split;
    }

    public Dimension getCanvasSize() {
        return canvasSize;
    }

    public AppPanel getWwjPanel() {
        return wwjPanel;
    }

    public WorldWindowGLCanvas getWwd() {
        return this.wwjPanel.getWwd();
    }

    public StatusBar getStatusBar() {
        return this.wwjPanel.getStatusBar();
    }
    //This panel contains the World Wind globe in a form of WorldWindowGLCanvas.
    //World Wind globe is a heavyweight component!

    public static class AppPanel extends JPanel {

        private static final long serialVersionUID = 426204280515L;
        protected WorldWindowGLCanvas wwd;
        protected StatusBar statusBar;

        public AppPanel(Dimension canvasSize, boolean includeStatusBar) {
            super(new BorderLayout());
            try{
            this.wwd = this.createWorldWindow();
            this.wwd.setPreferredSize(canvasSize);

            // Create the default model as described in the current worldwind properties.
            Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
            /*
            System.out.println(" Removing some layers ...");
            for (Layer l : m.getLayers()) {
                if (!l.getName().equals("NASA Blue Marble Image") && !l.getName().
                    equals("Blue Marble (WMS) 2004") && !l.getName().equals("Place Names") 
                    && !l.getName().equals("World Map") && !l.getName().equals("Scale bar") 
                    && !l.getName().equals("Compass")) {
                    m.getLayers().remove(l);
                }
            }
             * 
             */
            System.out.println("Setting model ...");
            this.wwd.setModel(m);

            // Setup a select listener for the worldmap click-and-go feature
            this.wwd.addSelectListener(new ClickAndGoSelectListener(this.getWwd(), WorldMapLayer.class));

            this.add(this.wwd, BorderLayout.CENTER);
            if (includeStatusBar) {
                this.statusBar = new StatusBar();
                this.add(statusBar, BorderLayout.PAGE_END);
                this.statusBar.setEventSource(wwd);
            }
            }
            catch(NullPointerException nex){
              nex.printStackTrace();  
            }
        }

        protected WorldWindowGLCanvas createWorldWindow() {
            return new WorldWindowGLCanvas();
        }

        public WorldWindowGLCanvas getWwd() {
            return wwd;
        }

        public StatusBar getStatusBar() {
            return statusBar;
        }
    }
}
