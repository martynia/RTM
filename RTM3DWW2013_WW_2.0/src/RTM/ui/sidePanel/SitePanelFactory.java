/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.sidePanel;

import RTM.layers.DataTransferSite;
import RTM.layers.GenericSite;
import RTM.layers.NetworkSite;
import RTM.layers.Site;
import javax.swing.JPanel;

/**
 *
 * @author martynia
 */
public class SitePanelFactory {

    static JPanel getSitePanel(GenericSite site) {
        if(site instanceof Site) {
            return new SitePanel((Site)site);
        } else if (site instanceof NetworkSite) {            
            return new NetworkSitePanel((NetworkSite)site);
        } else if (site instanceof DataTransferSite) {
            return new DataTransferSitePanel((DataTransferSite)site);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
