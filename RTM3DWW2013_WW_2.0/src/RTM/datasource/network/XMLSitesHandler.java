// XMLSitesHandler
package RTM.datasource.network;

import RTM.datasource.glite.*;
import RTM.layers.NetworkSite;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import java.util.Hashtable;

public class XMLSitesHandler extends DefaultHandler {

    private String current_value = "";
    private String id = "";
    private String name = "";
    private String longname = "";
    private String latitude = "";
    private String longitude = "";
    private String location = "";
    private String country = "";
    private String archiveName = "";
    private String hostname = "";
    private String logo = "";
    private String interfaceName = "";
    private String interfaceBW = "";
    private String homepage = "";
    private NetworkSite thisSite = null;
    private Hashtable<String, NetworkSite> Sites = new Hashtable<String, NetworkSite>();
    private String trafficPlot = "";

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (localName.equalsIgnoreCase("Site")) {
            // clear values
            id = "";
            name = "";
            longname = "";
            latitude = "";
            longitude = "";
            homepage = "";
            country = "";
            archiveName = "";
            hostname = "";
            interfaceName = "";
            interfaceBW = "";
            logo = "none";
            trafficPlot = "";

            thisSite = new NetworkSite();

        } else {
        }
    }

    public void endElement(String uri, String localName, String qName) {
        if (localName.equalsIgnoreCase("Site")) {
            thisSite.addMainInfo(id, name, longname, latitude, longitude, country);
            Sites.put(id, thisSite); // no id for NetworkSite (see below)
        } else {
            if (localName.equals("id")) {
                id = current_value.trim();
            } else {
                if (localName.equals("siteName")) {

                    name = current_value.trim();
                    id = name;
                } else {
                    if (localName.equals("organisationName")) {

                        longname = current_value.trim();
                    } else {
                        if (localName.equals("location")) {

                            location = current_value.trim();
                            thisSite.setLocation(location);
                        } else {
                            if (localName.equals("country")) {
                                country = current_value.trim();
                            } else {
                                if (localName.equals("latitude")) {
                                    latitude = current_value.trim();
                                } else {
                                    if (localName.equals("longitude")) {
                                        longitude = current_value.trim();
                                    } else {
                                        if (localName.equals("archiveName")) {
                                            archiveName = current_value.trim();
                                            thisSite.addArchiveName(archiveName);
                                        } else {
                                            if (localName.equals("interfaceName")) {
                                                interfaceName = current_value.trim();
                                                thisSite.addIfaceName(interfaceName);
                                            } else {
                                                if (localName.equals("interfaceBW")) {
                                                    interfaceBW = current_value.trim();
                                                    thisSite.addInterfaceBW(interfaceBW);
                                                } else {
                                                    if (localName.equals("hostName")) {
                                                        hostname = current_value.trim();
                                                        thisSite.addHostname(hostname);
                                                    } else {
                                                        if (localName.equals("logo")) {
                                                            logo = current_value.trim();
                                                            thisSite.addLogo(logo);
                                                        } else {
                                                            if (localName.equals("homepage")) {
                                                                homepage = current_value.trim();
                                                                thisSite.setWebpage(homepage);
                                                            } else {
                                                                 if (localName.equals("trafficPlot")) {
                                                                    trafficPlot = current_value.trim();
                                                                    thisSite.addTrafficPlot(trafficPlot);
                                                                 }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        current_value = "";
    }

    public void characters(final char[] ch, final int start, final int len) {
        current_value = current_value.concat(new String(ch, start, len));
    }

    public Hashtable<String, NetworkSite> getSites() {
        return Sites;
    }
}
