/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.network;

import RTM.RealTimeMonitor;
import RTM.layers.NetworkSite;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author martynia
 */
public class XMLDataHandler extends DefaultHandler {

    private String current_value = "";
    private String siteName="";
    private String timestamp="";
    private String rateIn="";
    private String rateOut="";

    public XMLDataHandler() {
        //populate();
    }

    private void populate() {
        // put job onto site
        double bytesIn = 1000, bytesOut = 800;
        try {
            NetworkSite site = RealTimeMonitor.getNetworkSites().get("GEANT PoPLon");
            site.addRates(bytesIn, bytesOut);
        } catch (Exception e) {
        } //  System.out.println( "non existent RB? " + rb ) ; }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        //System.out.println("qName:"+qName.toString()+" local name "+ localName.toString());
        if (localName.equals("observation")) {
            Logger.getLogger(XMLDataHandler.class.getName()).log(Level.FINE, "within observation)");
            //System.out.println("within observation)");
            siteName = "";
            timestamp = "";
            rateIn = "";
            rateOut = "";
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (localName.equals("observation")) {
            
            NetworkSite site = RealTimeMonitor.getNetworkSites().get(siteName);
            if (site != null) {
                try {
                    site.addRates(Double.parseDouble(rateIn), Double.parseDouble(rateOut));
                    Logger.getLogger(XMLDataHandler.class.getName()).log(Level.FINE," Site: "+site.getName()+ " Rate in: " + rateIn + " rateOut " + rateOut);
                } catch (NumberFormatException ex) {
                    Logger.getLogger(XMLDataHandler.class.getName()).log(Level.SEVERE, "Site: " + site.getName() + "Network rates non-floating point" , ex);
                }
            }
            Logger.getLogger(XMLDataHandler.class.getName()).log(Level.FINE,"done with observation for site >"+siteName+"<");
        } else {
            if (localName.equals("siteName")) {
                siteName = current_value.trim();
            } else if (localName.equals("observationDateTime")) {
                timestamp = current_value.trim();
            } else if (localName.equals("rateIn")) {
                rateIn = current_value.trim();
            } else if (localName.equals("rateOut")) {
                rateOut = current_value.trim();
            }
        }
        current_value = "";
    }

    @Override
    public void characters(final char[] ch, final int start, final int len) {
        current_value = current_value.concat(new String(ch, start, len));
    }
}
