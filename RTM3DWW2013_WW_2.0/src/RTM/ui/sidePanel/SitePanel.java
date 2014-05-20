/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.sidePanel;

import RTM.RealTimeMonitor;
import RTM.config.Config;
import RTM.datasource.panda.JobStatisticsHandler;
import RTM.layers.Site;
import RTM.ui.GridGuideButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Handle Site related information to be displayed on click.
 * @author Mikhail Khrypach, Janusz Martyniak
 */
public class SitePanel extends JPanel {
    private static final long serialVersionUID = 426204280515L;
    private JLabel numbersCE;
    private JLabel numbersRB;
    private JLabel pandaStatLabel = new JLabel();

    /**
     * Create a SitePanel object for the site to be placed as a tab of the
     * {@link SidePanel} or an inner frame (deprecated).
     * @param site requested site.
     */
    public SitePanel(final Site site) {
        super(new BorderLayout(10, 10));
        this.setBackground(Color.white);
        this.site = site;
        // determine if Transfers are enabled. This controls what stats/buttons/histograms are present.
        // Panda transfers
        boolean showp = RealTimeMonitor.getAf().getWwd().getModel().
                getLayers().getLayerByName("Panda Transfers Layer").isEnabled();
        //gLite transfers
        boolean showg = RealTimeMonitor.getAf().getWwd().getModel().
                getLayers().getLayerByName("Transfers Layer").isEnabled();

        // Branding
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.white);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        topPanel.setLayout(new BorderLayout(10, 10));

        // need this due to linewrap problems - JLabels never wrap so use that for the name - the longname can wrap...
        JPanel topWestPanel = new JPanel();
        topWestPanel.setBackground(Color.white);
        topWestPanel.setLayout(new BorderLayout(10, 10));

        String siteName = site.getName();
        String siteLongName = site.getLongName();
        JLabel name = new JLabel(siteName);
        topWestPanel.add(name, BorderLayout.NORTH);

        if (!siteLongName.equals(siteName)) {
            JTextArea longname = new JTextArea();
            longname.setLineWrap(true);
            longname.setWrapStyleWord(true);
            longname.append(siteLongName);
            longname.setEditable(false);
            longname.setFocusable(false);
            topWestPanel.add(longname, BorderLayout.CENTER);
        }

// GridGuide (optional)
        String gridGuide = site.getGridGuide();
        JButton gridGuideButton = new GridGuideButton(gridGuide, site).getButton();
        if (gridGuideButton != null) {
            topWestPanel.add(gridGuideButton, BorderLayout.SOUTH);
            // OK with external browser ? Firefox.
            //if(Config.isApplet()) gridGuideButton.setEnabled(false);  // disable for applets until we get
            // a signed browser code
        }

        topPanel.add(topWestPanel, BorderLayout.CENTER);
        // topEastPanel - Panda stats
        topEastPanel = new JPanel();
        topEastPanel.setBackground(Color.white);
        Border blackline = BorderFactory.createLineBorder(Color.black);
        topEastPanel.setBorder(BorderFactory.createTitledBorder(blackline, "Panda Job Statistics"));
        topEastPanel.setLayout(new BorderLayout(10, 10));
        topEastPanel.setVisible(showp); //only if Panda joba enabled
        pandaStatLabel = new JLabel("Scheduled = " + JobStatisticsHandler.getInstance().getScheduled(siteName)
                + " / Running = " + JobStatisticsHandler.getInstance().getRunning(siteName));
        topEastPanel.add(pandaStatLabel, BorderLayout.CENTER);

        topPanel.add(topEastPanel, BorderLayout.SOUTH);

        JLabel logoLabel = site.getLogoLabel();
        if (logoLabel != null) {
            topPanel.add(logoLabel, BorderLayout.EAST); // JM

        }
        this.add(topPanel, BorderLayout.NORTH);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 5, 2, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;

        GridBagConstraints d = (GridBagConstraints) c.clone();
        d.gridwidth = GridBagConstraints.RELATIVE;

        // CEs
        ArrayList CEs = site.getCEs();
        int numberOfCEs = CEs.size();
        // modify the gridbag object if many CEs (max 2 columns at present)
        // int ncolumns = numberOfCEs / 12 + 1;
        JPanel cePanel = new JPanel(gridbag);
        Border blueline = BorderFactory.createLineBorder(Color.blue);
        cePanel.setBorder(BorderFactory.createTitledBorder(blueline, "gLite CE stats & control"));
        Boolean left = true;
        if (numberOfCEs > 0) {

            cePanel.setBackground(Color.white);
            //cePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            numbersCE = new JLabel("Scheduled = " + site.getScheduledCE() + " / Running = " + site.getRunningCE());
            gridbag.setConstraints(numbersCE, c);
            cePanel.add(numbersCE);

            Iterator iterCE = CEs.iterator();
            while (iterCE.hasNext()) {
                String ip = (String) iterCE.next();
                JButton thisButtonCE = new JButton("show CE " + ip);
                thisButtonCE.setFocusable(false);
                thisButtonCE.setToolTipText(ip);
                thisButtonCE.setBackground(Color.white);
                thisButtonCE.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        //RTMSiteInFrame topParent = (RTMSiteInFrame) (((JButton) e.getSource()).getRootPane().getParent());
                        //topParent.setJobsFrame("ce", ((JButton) e.getSource()).getToolTipText());
                        RealTimeMonitor.showJobsFrame(site, "ce", ((JButton) e.getSource()).getToolTipText());
                    }
                });
                if (numberOfCEs > 12) {
                    if (left) {
                        gridbag.setConstraints(thisButtonCE, d);
                        left = false;
                    } else {
                        left = true;
                        gridbag.setConstraints(thisButtonCE, c);
                    }
                } else {
                    gridbag.setConstraints(thisButtonCE, c);
                }
                cePanel.add(thisButtonCE);
            }

            if (numberOfCEs > 1) {
                JButton total = new JButton("show all CEs");
                total.setFocusable(false);
                total.setToolTipText("all");
                total.setBackground(Color.white);
                total.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        //RTMSiteInFrame topParent = (RTMSiteInFrame) (((JButton) e.getSource()).getRootPane().getParent());
                        //topParent.setJobsFrame("ce", ((JButton) e.getSource()).getToolTipText());
                        RealTimeMonitor.showJobsFrame(site, "ce", ((JButton) e.getSource()).getToolTipText());
                    }
                });
                gridbag.setConstraints(total, c);
                cePanel.add(total);
            }

        }

        // RBs
        ArrayList RBs = site.getRBs();
        int numberOfRBs = RBs.size();
        left = true;
        JPanel rbPanel = new JPanel(gridbag);
        Border blueline2 = BorderFactory.createLineBorder(Color.blue);
        rbPanel.setBorder(BorderFactory.createTitledBorder(blueline2, "gLite WMS stats & control"));
        if (numberOfRBs > 0) {

            rbPanel.setBackground(Color.white);
            //rbPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            numbersRB = new JLabel("Scheduled = " + site.getScheduledRB() + " / Running = " + site.getRunningRB());
            gridbag.setConstraints(numbersRB, c);
            rbPanel.add(numbersRB);

            Iterator iterRB = RBs.iterator();
            while (iterRB.hasNext()) {
                String ip = (String) iterRB.next();
                JButton thisButtonRB = new JButton("show RB " + ip);
                thisButtonRB.setFocusable(false);
                thisButtonRB.setToolTipText(ip);
                thisButtonRB.setBackground(Color.white);
                thisButtonRB.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        //RTMSiteInFrame topParent = (RTMSiteInFrame) (((JButton) e.getSource()).getRootPane().getParent());
                        //topParent.setJobsFrame("rb", ((JButton) e.getSource()).getToolTipText());
                        RealTimeMonitor.showJobsFrame(site, "rb", ((JButton) e.getSource()).getToolTipText());
                    }
                });

                if (numberOfRBs > 12) {
                    if (left) {
                        gridbag.setConstraints(thisButtonRB, d);
                        left = false;
                    } else {
                        left = true;
                        gridbag.setConstraints(thisButtonRB, c);
                    }
                } else {
                    gridbag.setConstraints(thisButtonRB, c);
                }

                rbPanel.add(thisButtonRB);
            }

            if (numberOfRBs > 1) {
                JButton total = new JButton("show all RBs");
                total.setFocusable(false);
                total.setToolTipText("all");
                total.setBackground(Color.white);
                total.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        //RTMSiteInFrame topParent = (RTMSiteInFrame) (((JButton) e.getSource()).getRootPane().getParent());
                        //topParent.setJobsFrame("rb", ((JButton) e.getSource()).getToolTipText());
                        RealTimeMonitor.showJobsFrame(site, "rb", ((JButton) e.getSource()).getToolTipText());
                    }
                });
                gridbag.setConstraints(total, c);
                rbPanel.add(total);
            }
        }

        ceContainer = new JPanel();
        ceContainer.setBackground(Color.white);
        rbContainer = new JPanel();
        rbContainer.setBackground(Color.white);

        if ((numberOfCEs > 0) && (numberOfRBs > 0)) {
            ceContainer.add(cePanel);
            this.add(ceContainer, BorderLayout.WEST);
            rbContainer.add(rbPanel);
            this.add(rbContainer, BorderLayout.EAST);
        } else {
            if (numberOfCEs > 0) {
                ceContainer.add(cePanel);
                this.add(ceContainer, BorderLayout.WEST);
            } else {
                if (numberOfRBs > 0) {
                    rbContainer.add(rbPanel);
                    this.add(rbContainer, BorderLayout.WEST);
                }
            }
        }

        //  try {
        graphPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        if (showg) {
            addGridloadGraph(
                    "/gridload/" + site.getCountry() + "/" + site.getID() + "/Combined_CEs_Running_6_hours.png",
                    "gLite 6h");
            addGridloadGraph(
                    "/gridload/" + site.getCountry() + "/" + site.getID() + "/Combined_CEs_Running_1_day.png",
                    "gLite 1day");

        }



        if (showp) {
            topEastPanel.setVisible(true);
            addGridloadGraph("/gridload/Panda" + "/" + site.getID() + ".png",
                    "Panda 10h");
            addGridloadGraph("/gridload/Panda" + "/" + site.getID() + "_2d.png",
                    "Panda 2day");
        }


        this.setOpaque(true);
        this.validate();
    }

    private void addGridloadGraph(String serverPath, String graphTabLabel) {
        try {
            URI uri = new URI("http", Config.getWS_host(), serverPath, null);
            URL rrdtoolplotURL = uri.toURL();
            Image rrdtoolplotImage = Toolkit.getDefaultToolkit().createImage(rrdtoolplotURL);
            JLabel rrdtoolplotLabel = new JLabel(new ImageIcon(rrdtoolplotImage));
            graphPane.add(graphTabLabel, rrdtoolplotLabel);
            this.add(graphPane, BorderLayout.SOUTH);
            //jm this.add(rrdtoolplotLabel, BorderLayout.SOUTH);
            this.setPreferredSize(new Dimension(rrdtoolplotImage.getWidth(this), this.getHeight()));
        } catch (URISyntaxException ex) {
            Logger.getLogger(SitePanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SitePanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            System.out.println("Cannot find rrd graph?");
        }
    }

    public JTabbedPane getGraphsPane() {
        return graphPane;
    }

    public void customizeContent(ActionEvent e) {
       
        if (e.getActionCommand().equals("Panda Transfers Layer")) {
            if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {
                addGridloadGraph("/gridload/Panda" + "/" + site.getID() + ".png",
                        "Panda 10h");
                addGridloadGraph("/gridload/Panda" + "/" + site.getID() + "_2d.png",
                        "Panda 2day");
                pandaStatLabel.setText("Scheduled = " + JobStatisticsHandler.getInstance().getScheduled(site.getName())
                        + " / Running = " + JobStatisticsHandler.getInstance().getRunning(site.getName()));
                topEastPanel.setVisible(true);
            } else {
                int index = graphPane.indexOfTab("Panda 10h");
                System.out.println(" Panda tab layer index " + index);
                if (index != -1) {
                    graphPane.remove(index);
                }
                index = graphPane.indexOfTab("Panda 2day");
                if (index != -1) {
                    graphPane.remove(index);
                }
                topEastPanel.setVisible(false);
            }
        } else if (e.getActionCommand().equals("Transfers Layer")) {

            if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {
                addGridloadGraph(
                        "/gridload/" + site.getCountry() + "/" + site.getID() + "/Combined_CEs_Running_6_hours.png",
                        "gLite 6h");
                addGridloadGraph(
                        "/gridload/" + site.getCountry() + "/" + site.getID() + "/Combined_CEs_Running_1_day.png",
                        "gLite 1day");
//                we can assume the panels are in place, just make them visible:
                rbContainer.setVisible(true);
                ceContainer.setVisible(true);
//                if (rbContainer.getComponentCount() != 0 && ceContainer.getComponentCount() != 0) {
//                    this.add(ceContainer, BorderLayout.WEST);
//                    this.add(rbContainer, BorderLayout.EAST);
//                } else {
//                    if (rbContainer.getComponentCount() != 0) {
//                        this.add(rbContainer, BorderLayout.EAST);
//                    }
//                    if (ceContainer.getComponentCount() != 0) {
//                        this.add(ceContainer, BorderLayout.EAST);
//                    }
//                }
            } else {
                int index = graphPane.indexOfTab("gLite 6h");
                System.out.println(" gLite tab layer index " + index);
                if (index != -1) {
                    graphPane.remove(index);
                }
                index = graphPane.indexOfTab("gLite 1day");
                if (index != -1) {
                    graphPane.remove(index);
                }
                //System.out.println("rb? "+this.getComponentZOrder(rbContainer)+" ce? "+this.getComponentZOrder(ceContainer));
                rbContainer.setVisible(false);
                ceContainer.setVisible(false);
            }
        }
        System.out.println(e.getSource().toString());
    }
    private Site site;
    private final JTabbedPane graphPane;
    private JPanel topEastPanel;
    private JPanel ceContainer;
    private JPanel rbContainer;
    // todo semi automate this ...
    private String[] gLiteGraphs = {"gLite 6h"};
    private String[] pandaGraphs = {"Panda 10h"};
}
