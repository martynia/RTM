/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.sidePanel;

import RTM.charts.piechart.Simple2DPiechart;
import RTM.layers.NetworkSite;
import RTM.ui.BrowserLauncherButton;
import RTM.ui.GridGuideButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Display a NetworkSite information to be added to a SidePanel.
 * @author Janusz Martyniak
 */
class NetworkSitePanel extends JPanel {

    private static final long serialVersionUID = 426204280515L;
    private final JPanel topCenterPanel;
    private final JPanel topPanel;
    private final JPanel topSouthPanel;

    public NetworkSitePanel(NetworkSite networkSite) {

        super(new BorderLayout(10, 10));
        this.site = networkSite;
        this.setBackground(Color.white);

        // Branding
        topPanel = new JPanel();
        topPanel.setBackground(Color.white);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        topPanel.setLayout(new BorderLayout(10, 10));

        // need this due to linewrap problems - JLabels never wrap so use that for the name - the longname can wrap...
        this.topCenterPanel = new JPanel();
        topCenterPanel.setBackground(Color.white);
        topCenterPanel.setLayout(new BorderLayout(10, 10));

        String siteName = site.getName();
        String siteLongName = site.getLongName();
        JLabel name = new JLabel(siteName);
        topCenterPanel.add(name, BorderLayout.NORTH);

        if (!siteLongName.equals(siteName)) {
            JTextArea longname = new JTextArea();
            longname.setLineWrap(true);
            longname.setWrapStyleWord(true);
            longname.append(siteLongName);
            if (!site.getLocation().equals("")) {
                longname.append(", " + site.getLocation());
            }
            if (!site.getCountry().equals("")) {
                longname.append(", " + site.getCountry());
            }
            longname.setEditable(false);
            longname.setFocusable(false);
            topCenterPanel.add(longname, BorderLayout.CENTER);
        }

// GridGuide (optional)
        String gridGuide = site.getHomepage();
        if (gridGuide != null) {
            String logo = "old-go-home.png";
            JButton gridGuideButton = new BrowserLauncherButton(gridGuide, site, logo, site.getHomepage()).getButton();
            if (gridGuideButton != null) {
                topCenterPanel.add(gridGuideButton, BorderLayout.SOUTH);
            }
        }
        topPanel.add(topCenterPanel, BorderLayout.CENTER);
        // topCenterPanel - Panda stats
        this.topSouthPanel = new JPanel();
        this.topSouthPanel.setBackground(Color.white);
        Border blackline = BorderFactory.createLineBorder(Color.black);
        Date measurementDate = new Date(site.getTimestamp());
        this.topSouthPanel.setBorder(BorderFactory.createTitledBorder(blackline, "Network Traffic " + measurementDate.toString()));
        this.topSouthPanel.setLayout(new BorderLayout(10, 10));
        this.topSouthPanel.setVisible(true); //only if Panda joba enabled
        JTextArea headText = new JTextArea();
          
        String b_in = String.format("%9.2f",site.getRates().get(0));
        String b_out = String.format("%9.2f",site.getRates().get(1));
        
        headText.setText("Host: " + site.getHostname() + "\nInterface: " + site.getInterfaceName() + "\nBandwindh: " + site.getInterfaceBW());
        this.topSouthPanel.add(headText, BorderLayout.NORTH);
        JTextArea ratesText = new JTextArea("Rates: Mbits/s in:" + b_in + ",     Mbits/s out:" + b_out);
        this.topSouthPanel.add(ratesText, BorderLayout.CENTER);
        topPanel.add(this.topSouthPanel, BorderLayout.SOUTH);

        JLabel logoLabel = site.getLogoLabel();
        if (logoLabel != null) {
            topPanel.add(logoLabel, BorderLayout.EAST); // JM

        }
        this.add(topPanel, BorderLayout.NORTH);
        // Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        String[] labels = {"BytesIn", "BytesOut"};
        double[] values = {site.getRates().get(0).doubleValue(), site.getRates().get(1).doubleValue()};
        Simple2DPiechart pieChart = new Simple2DPiechart("Network Traffic", labels, values);
        pieChart.init();
        centerPanel.add(pieChart.get(), BorderLayout.CENTER);
        this.add(centerPanel, BorderLayout.CENTER);
        // traffic plot
        graphPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        URI uri;
        try {
            uri = new URI(site.getTrafficPlot());
            URL rrdtoolplotURL;
            rrdtoolplotURL = uri.toURL();
            Image rrdtoolplotImage = Toolkit.getDefaultToolkit().createImage(rrdtoolplotURL);
            JLabel rrdtoolplotLabel = new JLabel(new ImageIcon(rrdtoolplotImage));
            graphPane.add("Traffic Plot", rrdtoolplotLabel);
            this.add(graphPane, BorderLayout.SOUTH);
            this.setPreferredSize(new Dimension(rrdtoolplotImage.getWidth(this), this.getHeight()));
        } catch (MalformedURLException ex) {
            Logger.getLogger(NetworkSitePanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(NetworkSitePanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(NetworkSitePanel.class.getName()).log(Level.WARNING, "Missing Network Traffic plot?", ex);
        } catch (Exception ex) {
            Logger.getLogger(NetworkSitePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.setOpaque(true);
        this.validate();
    }
    private NetworkSite site;
    private final JTabbedPane graphPane;
}
