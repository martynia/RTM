/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM;

import RTM.ui.actions.PandaAction;
import RTM.ui.actions.GliteAction;
import RTM.ui.actions.AboutAction;
import RTM.ui.animations.FollowPath;
import RTM.ui.actions.HelpAction;
import RTM.ui.actions.HideMeAction;
import RTM.ui.actions.JobsAction;
import RTM.ui.actions.LayerSelectionAction;
import RTM.ui.actions.NetworkAction;
import RTM.ui.actions.PhedexAction;
import RTM.ui.actions.PlaybackAction;
import RTM.ui.actions.QuitAction;
import RTM.ui.actions.SmartPlaybackAction;
import RTM.ui.animations.Spin;
import RTM.ui.animations.SpinSpeed;
import RTM.ui.animations.SpinSpeedSelector;
import gov.nasa.worldwind.layers.Layer;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * Real Time Monitor Menu Bar
 * @author GridPP
 */
public class RTMMenuBar extends JMenuBar {

    private static final long serialVersionUID = 426204280515L;

    public static synchronized RTMMenuBar getInstance() {
        if (menubar == null) {
            menubar = new RTMMenuBar();
        }
        return menubar;
    }

    private RTMMenuBar() {
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem quit = new JMenuItem(new QuitAction());
        quit.setText("Exit");
        fileMenu.add(quit);
// Feeds menu
        JMenu feedsMenu = new JMenu("Feeds");
        feedsMenu.setMnemonic(KeyEvent.VK_F);
        JCheckBoxMenuItem jobs = new JCheckBoxMenuItem(new JobsAction("Jobs"));
        JCheckBoxMenuItem geant = new JCheckBoxMenuItem(new NetworkAction("Geant"));
        JCheckBoxMenuItem phedex = new JCheckBoxMenuItem(new PhedexAction("Phedex"));
        feedsMenu.add(jobs);
        feedsMenu.add(geant);
        feedsMenu.add(phedex);
// Jobs, Geant and Phedex Menus
        JMenu jobsMenu = new JMenu("Jobs");
        JCheckBoxMenuItem gliteJobs = new JCheckBoxMenuItem(new GliteAction("gLite"));
        JCheckBoxMenuItem pandaJobs = new JCheckBoxMenuItem(new PandaAction("Panda"));
        jobsMenu.add(gliteJobs);
        jobsMenu.add(pandaJobs);
        jobsMenu.setEnabled(true);

        Component[] menuComponents = feedsMenu.getMenuComponents();
        System.out.println(" no of components " + menuComponents.length);
        for (int c = 0; c < menuComponents.length; c++) {
            if (menuComponents[c] instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem item = ((JCheckBoxMenuItem) menuComponents[c]);
                if (item.getText().equals("Jobs")) {
                    System.out.println(" Item Jobs found ");
                    // item.doClick();   
                }

            }
        }

        JMenu geantMenu = new JMenu("Geant");
        JMenu phedexMenu = new JMenu("Phedex");
// Layers Menu
        JMenu layersMenu = new JMenu("Layers");
        layersMenu.setMnemonic(KeyEvent.VK_L);
// Animations Menu

        JMenu animMenu = new JMenu("Animations");
        ButtonGroup animGroup = new ButtonGroup();

        JRadioButtonMenuItem stopAnimMenuItem = new JRadioButtonMenuItem(("None"));
        stopAnimMenuItem.setSelected(true);  // no animations by default
        animGroup.add(stopAnimMenuItem);
        animMenu.add(stopAnimMenuItem);
        // the message passed between speed submneus and the Spin object, so the wait() in
        // Spin is interrupter and a new speed applied immediately.
        SpinSpeed message = new SpinSpeed();

        SpinSpeedSelector spinSpeedSelector = new SpinSpeedSelector("Full turn in ..", message);
        JMenu spinSpeed = new JMenu(spinSpeedSelector);
        spinSpeedSelector.addMenuItems(spinSpeed);


        spin = new JRadioButtonMenuItem("Spin!");

        spin.addItemListener(new Spin(message, spinSpeed));
        // ( we need to pass the spinSpeed reference here to enable/disable the
        // spinSpeed menu when Spin is enabled/disabled)
        spinSpeed.setEnabled(spin.isSelected()); // intial setting

        //spin.setArmed(true);
        animGroup.add(spin);
        animMenu.add(spin);

        // add the speed selector menu
        animMenu.add(spinSpeed);
        //
        followPath = new JRadioButtonMenuItem("Points of Interest");
        followPath.addItemListener(new FollowPath());
        animGroup.add(followPath);
        animMenu.add(followPath);
// Playback Menu
        JMenu pbMenu = new JMenu("Playback");
        //pbMenu.setForeground(Color.green);
        pbMenu.setMnemonic(KeyEvent.VK_P);
        JMenuItem playback = new JCheckBoxMenuItem(new PlaybackAction());
        playback.setText("On..");
        JMenuItem smartPbk = new JCheckBoxMenuItem(new SmartPlaybackAction());
        smartPbk.setText("Auto");
        pbMenu.add(playback);
        pbMenu.add(smartPbk);
        
// Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        JMenuItem help = new JMenuItem(new HelpAction());
        help.setText("Help");
        JMenuItem about = new JMenuItem(new AboutAction());
        //about.setText("About");
        helpMenu.add(help);
        helpMenu.add(about);

        // layers menu items
        for (Layer l : RealTimeMonitor.getAf().getWwd().getModel().getLayers()) {
            JCheckBoxMenuItem mi = new JCheckBoxMenuItem(); // labels are defined by actions
            mi.setAction(new LayerSelectionAction(l)); // quickly for now, l has to be application-wide
            if (l.getName().equals("NASA Blue Marble Image")) {
                mi.setEnabled(false); // some layers are initially disabled
                // World Wind layers have only one 'enable' level, i.e enabled
                // means present in the picture. We treat it as 'disabled' in the
                // Swing speak. Have to do it explicitely, since some layers might
                // be un-selected initially (not checked) by command line switches,
                // so we cannot do: mi.setEnabled(!l.isEnabled))
            }
            String lname = l.getName();
            if (lname.startsWith("FTS") || lname.startsWith("Panda") || lname.startsWith("Sites Layer")
                    || lname.startsWith("Transfers") || lname.startsWith("Network Sites")) {
                continue;
            }
            mi.setSelected(l.isEnabled());
            layersMenu.add(mi);
        }

        helpMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        fileMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        layersMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        feedsMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        jobsMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        geantMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        phedexMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        animMenu.getPopupMenu().setLightWeightPopupEnabled(false);
// Hide Me button
        JButton hideMe = new JButton(new HideMeAction(this));
        hideMe.setFocusable(false);
        this.add(fileMenu);
        this.add(feedsMenu);
        this.add(jobsMenu);
        this.add(geantMenu);
        this.add(phedexMenu);
        this.add(layersMenu);
        this.add(Box.createGlue());
        this.add(hideMe);
        this.add(animMenu);
        this.add(pbMenu);
        this.add(helpMenu);

        this.setFocusTraversalPolicyProvider(true);
        this.setFocusCycleRoot(true);
        this.setFocusTraversalKeysEnabled(true);
    }
    private static RTMMenuBar menubar = null;
    private JRadioButtonMenuItem spin;
    private JRadioButtonMenuItem followPath;

    public AbstractButton getSpinButton() {
        return spin;
    }

    public AbstractButton getFollowPathButton() {
        return followPath;
    }

    void customize() {
        // default: enable Jobs (gLite & Panda)
        //
        JCheckBoxMenuItem jobs = null;
        boolean anyFeed = false;
        //
        int m = this.getMenuCount();
        System.out.println(" Number of menus in the menubar " + m);
        for (int i = 0; i < m; i++) {
            JMenu menu = getMenu(i);
            if (menu == null) {
                continue;
            }
            if (menu.getText().equals("Feeds")) {
                int subm = menu.getItemCount();
                System.out.println(" Feeds #submenus " + subm);
                for (int k = 0; k < subm; k++) {
                    if (menu.getItem(k) instanceof JCheckBoxMenuItem) {
                        JCheckBoxMenuItem mi = (JCheckBoxMenuItem) menu.getItem(k);
                        String value = System.getProperty(mi.getText());
                        if (value != null) {
                            if (value.equalsIgnoreCase("true")) {
                                mi.doClick();
                                anyFeed = true;
                            }
                        }

                        if (mi.getText().equals("Jobs")) {
                            jobs = mi;  // click on Jobs, this is the default.
                        }

                    } else {
                    }
                }
                // the deafault is only active if none of other Feeds options is enabled.
                if (jobs != null && !anyFeed && !jobs.isSelected()) {
                    jobs.doClick();
                    System.out.println(" Deafault Feed enabled ");
                }
            }
        }
    }
}
