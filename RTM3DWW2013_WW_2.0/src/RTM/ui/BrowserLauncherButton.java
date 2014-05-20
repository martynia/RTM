/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui;

import RTM.Browser.ExternalBrowser;
import RTM.layers.GenericSite;
import java.awt.Color;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * A helper class to handle an optional Browser launch button
 * @author Janusz Martyniak
 */
public class BrowserLauncherButton {

    private ImageIcon gridGuideIcon;
    private String gridGuideIconName = "GG-logo.jpg";
    private JButton gridGuideButton;
    /**
     * Creates a WEB browser launch button
     * @param address  Website address
     * @param site     a site the button is allocated to
     * @param iconName button icon
     * @param iconText button text 
     */
    public BrowserLauncherButton(final String address, final GenericSite site, final String iconName, final String iconText) {
 
        if (address.startsWith("http")) {
            try {
                URL ggURL = this.getClass().getResource(gridGuideIconName);
                if (ggURL != null) {
                    gridGuideIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(iconName)));
                }
            } catch (Exception ex) {
                System.out.println(" *********icon fetch failed *************");
                ex.printStackTrace();
            }
            gridGuideButton = new JButton();
            gridGuideButton.setFocusable(false);
            if (gridGuideIcon != null) {
                gridGuideButton.setIcon(gridGuideIcon);
                gridGuideButton.setText(iconText);
            } else {
                gridGuideButton.setText(" Website: "+iconText);
            }
            gridGuideButton.setBackground(Color.WHITE);
            gridGuideButton.setToolTipText("This will open an external default browser window");
            gridGuideButton.addActionListener(new ExternalBrowser(address));
            
        }
        
    }
    /**
     * Returns a button to launch a Web browser.
     * @return a JButton instance.
     */
    public JButton getButton() {
        return gridGuideButton;
    }
}