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
 * A helper class to handle an optional GridGuide button
 * @author Janusz Martyniak
 */
public class GridGuideButton extends BrowserLauncherButton {

    public GridGuideButton(final String gridGuide, final GenericSite site) {
        super(gridGuide, site, "GG-logo.jpg", " for the Site");
        if (getButton() != null) {
            getButton().setToolTipText("This will open an embedded browser window with GridGuide information about the site");
        }
    }
}