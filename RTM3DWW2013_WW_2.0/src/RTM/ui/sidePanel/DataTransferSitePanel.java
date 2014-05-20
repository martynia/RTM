/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.sidePanel;

import RTM.config.Config;
import RTM.layers.CMSFileTransferSite;
import RTM.layers.DataTransferSite;
import RTM.util.ImageUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author martynia
 */
class DataTransferSitePanel extends JPanel {

    private static final long serialVersionUID = 426204280515L;
    private DataTransferSite site = null;
    private final JPanel topPanel;
    private final JPanel topCenterPanel;
    private final JPanel centerPanel;
    //private final JTabbedPane graphPane;
    //private final JTabbedPane cmsSiteGraphPane;
    private JTabbedPane cmsSiteGraphPane = null;

    public DataTransferSitePanel(DataTransferSite dataTransferSite) {
        super(new BorderLayout(10, 10));
        this.site = dataTransferSite;
        this.setBackground(Color.white);


        topPanel = new JPanel();
        topPanel.setBackground(Color.white);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        topPanel.setLayout(new BorderLayout(10, 10));

        topCenterPanel = new JPanel();
        topCenterPanel.setBackground(Color.white);
        topCenterPanel.setLayout(new BorderLayout(10, 10));
        // for transfer sites name is used to 
        // store a WLCG name (CERN-PROD for example)
        String siteName = site.getName();
        String siteLongName = " WLCG site name " + site.getName();
        JLabel name = new JLabel(siteName);
        topCenterPanel.add(name, BorderLayout.NORTH);

        topPanel.add(topCenterPanel, BorderLayout.NORTH);

        centerPanel = new JPanel();
        centerPanel.setBackground(Color.white);
        centerPanel.setLayout(new BorderLayout(10, 10));

        // create a tabbed pane (graphPane) first, to host more than one graph type for a single site
        // this is a CMS pane, it holds 2 graphs each.
        // cmsSiteGraphPane - holds 1 cms site in each tab
        System.out.println(" Data Transfer Site " + site.getName() + "\n");
        cmsSiteGraphPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        for (CMSFileTransferSite cmsSite : site.getCMSFileTransferSites().values()) {
            System.out.println(" Data Transfer SUB-Site " + cmsSite.getName() + "\n");
            JTabbedPane graphPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
            addGraphToPane(graphPane, "/dynamic_information/phedex/images/from_" + cmsSite.getName() + ".png", "out:" + cmsSite.getName());
            addGraphToPane(graphPane, "/dynamic_information/phedex/images/to_" + cmsSite.getName() + ".png", "in:" + cmsSite.getName());
            cmsSiteGraphPane.add(graphPane, cmsSite.getName());

        }
        centerPanel.add(cmsSiteGraphPane, BorderLayout.CENTER);
        //sitePane.add(cmsSiteGraphPane, cmsSite.getName());

        // additional site text at the bottom:
        JTextArea siteText = new JTextArea();
        siteText.setLineWrap(true);
        siteText.setWrapStyleWord(true);
       // if (!site.getCountry().equals("")) {
       //     siteText.append(" Country: " + site.getCountry());
       // }

        siteText.append("\n\n" + site.getRatesAsSstring()); // just prints global rates
       // add links rates
        HashMap<String, CMSFileTransferSite> subsites = site.getCMSFileTransferSites();
        
        for( CMSFileTransferSite cmsSite : subsites.values()) {
            siteText.append(cmsSite.toString());
        }
        //siteText.append(site.getCMSFileTransferSites().toString()); 
       //
        siteText.setEditable(false);
        siteText.setFocusable(false);
        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.white);
        textPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Country: "+site.getCountry()+", "+site.getName()+ " Statistics (last hour)"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        textPanel.setLayout(new BorderLayout(10,10));
        textPanel.add(siteText, BorderLayout.CENTER);
        centerPanel.add(textPanel, BorderLayout.SOUTH);
        //centerPanel.add(siteText, BorderLayout.SOUTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.NORTH);

        this.setOpaque(true);
        this.validate();
    }

    private void addGraphToPane(JTabbedPane pane, String serverPath, String graphTabLabel) {
        try {
            JLabel plotLabel;

            URI uri = new URI("http", Config.getWS_host(), serverPath, null);
            URL plotURL = uri.toURL();
            BufferedImage plotImage = ImageIO.read(plotURL); //   Toolkit.getDefaultToolkit().createImage(plotURL);
            //resize only, if wider than 50% of the frame and taller than 70% of the frame height:
            if (plotImage.getWidth() > Config.getFrameWidth() / 2 || plotImage.getHeight() > 0.7*Config.getFrameHeight()) {
                plotImage = ImageUtilities.resizeImage(plotImage, Config.getFrameWidth() / 3, Config.getFrameHeight() / 3);
            }
            plotLabel = new JLabel(new ImageIcon(plotImage));
            pane.add(graphTabLabel, plotLabel);
            centerPanel.setPreferredSize(new Dimension(plotImage.getWidth(this), this.getHeight()));
        } catch (URISyntaxException ex) {
            Logger.getLogger(DataTransferSitePanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DataTransferSitePanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            Logger.getLogger(DataTransferSitePanel.class.getName()).log(Level.SEVERE, "Cannot load a graph ?", e);
        }
    }
}
