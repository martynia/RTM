package RTM.ui.sidePanel;

import RTM.RealTimeMonitor;
import RTM.layers.GenericSite;
import RTM.layers.Pickable;
import RTM.layers.Site;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
//import gov.nasa.worldwind.view.orbit.
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.OrbitView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Side panel is used to display all data and controls of the application,
 * when it's run in 'sidePanel = true' mode (internal frames are used otherwise)
 * @see AppFrame
 * @author Mikhail Khrypach
 */
public class SidePanel extends JPanel {

    private static final long serialVersionUID = 426204280515L;
    //Side panel uses tabs to organise multiple instances of data/controls panels
    private final JTabbedPane tabPnl;
    //close button hides sidePanel and closes all of its components.
    private JButton clBtn;
    //!!!closing is used in order to eliminate errors with listeners when something
    //in sidePanel closes (i.e. change listener)
    private boolean closing = false;
    //about panel
    private AboutPanel aboutPnl;
    private HelpPanel helpPnl;

    /**
     * A Panel to hold a selection of tabbed SitePanel instances or RB/VO/Jobs selection information. 
     */
    public SidePanel() {
        super(new BorderLayout());
        this.setFocusable(false);
        this.setBackground(Color.black);
        tabPnl = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        aboutPnl = new AboutPanel();
        helpPnl = new HelpPanel();
        tabPnl.setFocusable(false);
        tabPnl.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {

                if (!closing) {
                    SidePanel.this.setPreferredSize(SidePanel.this.getTabPnl().getSelectedComponent().getPreferredSize());
                    //RTM.getSplit().resetToPreferredSizes();
                    RealTimeMonitor.getSplit().setDividerLocation((int) SidePanel.this.getPreferredSize().getWidth() * 10 / 9);
                    SidePanel.this.validate();
                    Site s = RealTimeMonitor.getSites().get(tabPnl.getTitleAt(tabPnl.getSelectedIndex()));
                    if (s != null) {
                        OrbitView view = (OrbitView) RealTimeMonitor.getAf().getWwd().getView();
                        Globe globe = RealTimeMonitor.getAf().getWwd().getModel().getGlobe();
                        //JM - changed according to the new (ww-1.2 ) View/Animator interface architecture
                        ((BasicOrbitView) view).addPanToAnimator(s.getPosition(), view.getHeading(), view.getPitch(), 700000);
                       // view.applyStateIterator(FlyToOrbitViewStateIterator.createPanToIterator(
                       //         view, globe, s.getPosition(), view.getHeading(), view.getPitch(), 700000));
                    }
                }
            }
        });
        this.add(tabPnl);
        addCloseBtn();
        this.setVisible(false);
    }

    /**
     * Ads the close button when constructing the tabbed pane.
     */
    void addCloseBtn() {
        clBtn = new JButton("Close");
        clBtn.setFocusable(false);
        clBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                closing = true;
                SidePanel pnl = SidePanel.this;
                pnl.setVisible(false);
                pnl.getTabPnl().removeAll();
                closing = false;
            }
        });
        this.add(clBtn, BorderLayout.NORTH);
    }

    /**
     * Adds a site tab with RB/CE buttons and a job numbers histogram.
     * The action is performed when a user clicks on the map close to a site.
     * @param site the Site object to add.
     */
    public void addSite(GenericSite site) {
        int i = tabPnl.indexOfTab(site.getID());
        //Checking if the instance's been already opened
        if (i == -1) {
            JPanel sp = SitePanelFactory.getSitePanel(site);
            JScrollPane sitePnl = new JScrollPane(sp);
            sitePnl.setPreferredSize(sp.getPreferredSize());
            //this.setPreferredSize(sp.getPreferredSize());
            tabPnl.addTab(site.getID(), sitePnl);
            tabPnl.setTabComponentAt(tabPnl.getTabCount() - 1, createTabComp(site.getName()));
            tabPnl.setSelectedComponent(sitePnl);
            sitePnl.validate();
        } else {
            tabPnl.setSelectedIndex(i);
        }
        this.setVisible(true);
    }

    /**
     * Creates and displays the RB control panel which allows a user to select
     * only sites/transfers for selected RBs.
     */
    public void showRBControlsPanel() {
        String rcpName = "RB Controls";
        int i = tabPnl.indexOfTab(rcpName);
        //Checking if the instance's been already opened
        if (i == -1) {
            RBControlsPanel rcp = new RBControlsPanel();
            rcp.setBackground(Color.BLACK);
            tabPnl.addTab(rcpName, rcp);
            tabPnl.setTabComponentAt(tabPnl.getTabCount() - 1, createTabComp(rcpName));
            tabPnl.setSelectedComponent(rcp);
        } else {
            tabPnl.setSelectedIndex(i);
        }
        this.setVisible(true);
    }

    /**
     * Creates and displays the VO control panel which allows a user to select
     * only sites/transfers for selected VOs.
     */
    public void showVOControlsPanel() {
        String vcpName = "VO Controls";
        int i = tabPnl.indexOfTab(vcpName);
        //Checking if the instance's been already opened
        if (i == -1) {
            VOControlsPanel vcp = new VOControlsPanel();
            vcp.setBackground(Color.BLACK);
            tabPnl.addTab(vcpName, vcp);
            tabPnl.setTabComponentAt(tabPnl.getTabCount() - 1, createTabComp(vcpName));
            tabPnl.setSelectedComponent(vcp);
        } else {
            tabPnl.setSelectedIndex(i);
        }
        this.setVisible(true);
    }

    /**
     * Create and display a panel with jobs for a given site.
     * @see JobsPanel
     * @param site site to use.
     * @param type could be <code>'ce'</code> (otherwise <code>'rb'</code>)
     * @param ip   hostname of teh CE (or RB) depending on <code>type</code>
     */
    public void showJobsPanel(Site site, String type, String ip) {
        String jpName = site.getName() + "--" + type + "--" + ip;
        int i = tabPnl.indexOfTab(jpName);
        //Checking if the instance's been already opened
        if (i == -1) {
            JScrollPane jp = new JScrollPane(new JobsPanel(site, type, ip));
            jp.setBackground(Color.BLACK);
            tabPnl.addTab(jpName, jp);
            tabPnl.setTabComponentAt(tabPnl.getTabCount() - 1, createTabComp(jpName));
            tabPnl.setSelectedComponent(jp);
        } else {
            tabPnl.setSelectedIndex(i);
        }
        this.setVisible(true);
    }

    /**
     * Create and display the <code>About</code> information screen. The screen
     * is normally displayed when a user presses the <code>a</code> key and also at
     * program startup.
     */
    public void showAboutPanel() {
        String apName = "About";
        int i = tabPnl.indexOfTab(apName);
        if (1.0 * RealTimeMonitor.getAf().getWidth() / RealTimeMonitor.getAf().getHeight() > 1.5) { //wide screens
            aboutPnl.setPreferredSize(new Dimension(RealTimeMonitor.getAf().getWidth() / 3, this.getHeight()));
        } else {
            aboutPnl.setPreferredSize(new Dimension(RealTimeMonitor.getAf().getWidth() / 2, this.getHeight()));
        }
        //Checking if the instance's been already opened
        if (i == -1) {
            tabPnl.addTab(apName, aboutPnl);
            tabPnl.setTabComponentAt(tabPnl.getTabCount() - 1, createTabComp(apName));
            tabPnl.setSelectedComponent(aboutPnl);
            Thread wd = new Thread(new TabLifetimeWatchdog());
            wd.start();
        } else {
            tabPnl.setSelectedIndex(i);
        }
        this.setVisible(true);
    }

    /**
     * Create and display a help information screen. The screen is normally
     * displayed when a user presses the <code>h</code> key.
     */
    public void showHelpPanel() {
        String apName = "Help";
        int i = tabPnl.indexOfTab(apName);
        aboutPnl.setPreferredSize(new Dimension(RealTimeMonitor.getAf().getWidth() / 2, this.getHeight()));
        //Checking if the instance's been already opened
        if (i == -1) {
            tabPnl.addTab(apName, helpPnl);
            tabPnl.setTabComponentAt(tabPnl.getTabCount() - 1, createTabComp(apName));
            tabPnl.setSelectedComponent(helpPnl);
        } else {
            tabPnl.setSelectedIndex(i);
        }
        this.setVisible(true);
    }

    /**
     * Obtain the side tabbed panel reference.
     * @return the reference to the TabbedPane.
     */
    public JTabbedPane getTabPnl() {
        return this.tabPnl;
    }

    /**
     * Obtain a reference to the side panel close button.
     * @return the JButton reference
     */
    public JButton getCloseButton() {
        return this.clBtn;
    }

    //Attaches a label with an appropriate name and a close button to each tab
    private JPanel createTabComp(String name) {
        final JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(name);
        l.setVisible(true);
        p.add(l);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        JButton b = new JButton();
        int size = 17;
        b.setPreferredSize(new Dimension(size, size));
        b.setToolTipText("close this tab");
        b.setContentAreaFilled(false);
        b.setFocusable(false);
        b.setBorder(BorderFactory.createEtchedBorder());
        b.setBorderPainted(false);
        b.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                closing = true;
                int i = tabPnl.indexOfTabComponent(p);
                if (i != -1) {
                    tabPnl.remove(i);
                }
                if (tabPnl.getTabCount() == 0) {
                    clBtn.doClick();
                }
                closing = false;
            }
        });
        b.setText("x");
        b.setVisible(true);
        p.add(b);
        p.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        p.setVisible(true);
        return p;
    }

    /**
     * A class to handle a watchdog to automatically close the side panel when
     * only certain tabes are displayed. At the moment only a lone About tab undergoes
     * the automatic removal...
     */
    private class TabLifetimeWatchdog implements Runnable {

        public TabLifetimeWatchdog() {
            wait = 0;
        }

        public void run() {
            // if only "About" present as a lone tab, it will disappear automatically
            // after certain time. (the whole tabbed panel will close)
            int index = -1;
            while (wait < 4 * nap) {
                index = tabPnl.indexOfTab("About");
                try {
                    Thread.sleep(nap);
                    if (tabPnl.getTabCount() == 1 && index != -1) {
                        wait += nap; // count 'on' time
                    } else if (tabPnl.indexOfTab("About") != -1) {
                        wait = 0; // suspend countdown
                    } else {
                        // Panel empty or not empty, but no 'About'
                        return;  // quit, this terminates the thread. OK
                    }
                    Logger.getLogger(SidePanel.class.getName()).log(Level.FINE," wait time passed " + wait);

                } catch (InterruptedException ex) {
                    Logger.getLogger(SidePanel.class.getName()).log(Level.SEVERE, "Interrupted ..returning", ex);
                    return;
                }
            }
            if (tabPnl.indexOfTab("About") != -1) {
                getCloseButton().doClick();
            }
        }
        private int wait;
        private int nap = 21000;
    }
}
