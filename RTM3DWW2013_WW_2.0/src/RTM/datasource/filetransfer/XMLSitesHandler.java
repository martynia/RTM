// XMLSitesHandler
package RTM.datasource.filetransfer;

import RTM.datasource.glite.*;
import RTM.layers.CMSFileTransferSite;
import RTM.layers.DataTransferSite;
import RTM.layers.GenericSite;
import RTM.layers.NetworkSite;
import java.util.HashMap;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XMLSitesHandler extends DefaultHandler {

    private String current_value = "";
    private String id = "";
    private String name = "";
    private String cms_name = "";
    private String latitude = "";
    private String longitude = "";
    private String location = "";
    private String country = "";
    private String longname = "";
    private Hashtable<String, DataTransferSite> Sites = new Hashtable<String, DataTransferSite>();
    private HashMap<String, String> reverseMap = new HashMap<String, String>();
    private DataTransferSite thisSite=null;

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (localName.equalsIgnoreCase("item")) {
            // clear values
            id = "";
            name = "";
            cms_name = "";
            longname = "";
            latitude = "";
            longitude = "";
            country = "";

        } else {
        }
    }

    public void endElement(String uri, String localName, String qName) {
        if (localName.equalsIgnoreCase("item")) {
            // we have to borrow glite site position from to our CMS sites
            GenericSite gliteSite = RTM.RealTimeMonitor.getSites().get(name);
            if (gliteSite != null) {
                longname = gliteSite.getLongName();
                latitude = new Double(gliteSite.getLatitude()).toString();
                longitude = new Double(gliteSite.getLongitude()).toString();
                country = gliteSite.getCountry();
                // id==name, hold CMS names in a subsite list
                if(Sites.containsKey(name)) {
                    thisSite = Sites.get(name);
                } else {
                   thisSite = new DataTransferSite();
                   thisSite.addMainInfo(name, name, longname, latitude, longitude, country);
                }
                // we see this CMS name for the first time, so after getting/creating a DataTransferSite, add the
                // first CMS subsite
                thisSite.getCMSFileTransferSites().put(cms_name, new CMSFileTransferSite(cms_name));
                Sites.put(name, thisSite);
                reverseMap.put(cms_name, name);
                Logger.getLogger(XMLDataHandler.class.getName()).log(Level.FINE," Adding "+Sites.size()+ "'th site "+cms_name+ "<->"+ name);
            } else {
                Logger.getLogger(XMLDataHandler.class.getName()).log(Level.WARNING,"FileTransferSite "+ cms_name + " has no gLite "+ name +" counterpart");
            }

        } else {
            if (localName.equals("cms_name")) {
                cms_name = current_value.trim();
            } else {
                if (localName.equals("sam_name")) {

                    name = current_value.trim();
                } else {
                }
            }

            current_value = "";
        }
    }

    public void characters(final char[] ch, final int start, final int len) {
        current_value = current_value.concat(new String(ch, start, len));
    }

    public Hashtable<String, DataTransferSite> getSites() {
        return Sites;
    }
    public HashMap<String, String> getReverseSiteMap() {
        return reverseMap;
    }
}
