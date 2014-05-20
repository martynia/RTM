/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui;

import RTM.RealTimeMonitor;
import RTM.config.Config;
import RTM.layers.Site;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Internal frames instances, display all information about the site the frame,
 * belongs to. The frame's creation is initiated from its site.
 * @author Mikhail Khrypach
 */
public class RTMSiteInFrame extends JInternalFrame {
    
    private static final long serialVersionUID = 15051973L;
    private final Site site;
    private Icon gridGuideIcon = null;
    private JLabel numbersCE;
    private JLabel numbersRB;

    public RTMSiteInFrame(final Site site) {
        super("Site: " + site.getName(),
                false, //resizable
                true, //closable
                false, //maximizable
                true);
        this.site = site;
        this.setBackground(Color.white);

        this.setLayout(new BorderLayout(10, 10));

        this.addInternalFrameListener(new RTMInFrameAdapter(site));
        this.addKeyListener(RealTimeMonitor.getKeyAdapter());

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
        if(gridGuideButton!=null) topWestPanel.add(gridGuideButton, BorderLayout.SOUTH);
        
        topPanel.add(topWestPanel, BorderLayout.CENTER);

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
        Boolean left = true;
        if (numberOfCEs > 0) {

            cePanel.setBackground(Color.white);
            cePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

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
        if (numberOfRBs > 0) {

            rbPanel.setBackground(Color.white);
            rbPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

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

        if ((numberOfCEs > 0) && (numberOfRBs > 0)) {
            JPanel ceContainer = new JPanel();
            ceContainer.setBackground(Color.white);
            ceContainer.add(cePanel);
            this.add(ceContainer, BorderLayout.WEST);
            JPanel rbContainer = new JPanel();
            rbContainer.setBackground(Color.white);
            rbContainer.add(rbPanel);
            this.add(rbContainer, BorderLayout.EAST);
        } else {
            if (numberOfCEs > 0) {
                this.add(cePanel, BorderLayout.WEST);
            } else {
                if (numberOfRBs > 0) {
                    this.add(rbPanel, BorderLayout.WEST);
                }
            }
        }

        try {
            String logoAddress = new String(Config.getWS_URL()+"/gridload/" + site.getCountry() + "/" + site.getID() + "/Combined_CEs_Running_6_hours.png");
            URL rrdtoolplotURL = new URL(logoAddress);
            Image rrdtoolplotImage = Toolkit.getDefaultToolkit().createImage(rrdtoolplotURL);
            JLabel rrdtoolplotLabel = new JLabel(new ImageIcon(rrdtoolplotImage));
            this.add(rrdtoolplotLabel, BorderLayout.SOUTH);
        } catch (Exception e) {
            System.out.println("Cannot find rrd graph?");
        }

        this.setSize(400, 300);
        this.setLocation(RealTimeMonitor.getInframePosition());
        this.setOpaque(true);
        this.pack();
    }
}
