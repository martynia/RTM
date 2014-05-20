/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.filetransfer;

import RTM.config.Config;
import RTM.datasource.reader.Reader;
import RTM.layers.DataTransferSite;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *
 * @author martynia
 */
public class FileTransferSites {

    public FileTransferSites() {
        Reader reader = new Reader(Config.getWS_URL() + "/dynamic_information/cms_to_wlcg.xml");
        fileSiteHandler = new RTM.datasource.filetransfer.XMLSitesHandler();
        reader.read(fileSiteHandler);
    }
    public Hashtable<String, DataTransferSite> getSites() {
        return fileSiteHandler.getSites();
    }
    public HashMap<String, String> getReverseSiteMap() {
        return fileSiteHandler.getReverseSiteMap();
    }
    private RTM.datasource.filetransfer.XMLSitesHandler fileSiteHandler ;
}
