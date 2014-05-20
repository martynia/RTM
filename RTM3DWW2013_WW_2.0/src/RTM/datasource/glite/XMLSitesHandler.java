// XMLSitesHandler
package RTM.datasource.glite;

import RTM.layers.Site;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import java.util.Hashtable;

public class XMLSitesHandler extends DefaultHandler {

    private boolean within_id = false;
    private boolean within_name = false;
    private boolean within_longname = false;
    private boolean within_latitude = false;
    private boolean within_longitude = false;
    private boolean within_site = false;
    private boolean within_ce = false;
    private boolean within_rb = false;
    private boolean within_logo = false;
    private String current_value = "";
    private String id = "";
    private String name = "";
    private String longname = "";
    private String latitude = "";
    private String longitude = "";
    private String country = "";
    private String ce = "";
    private String rb = "";
    private String logo = "";
    private String gridguide = "";
    private Site thisSite = null;
    private Hashtable<String, Site> Sites = new Hashtable<String, Site>();

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("Site")) {
            // clear values
            id = "";
            name = "";
            longname = "";
            latitude = "";
            longitude = "";
            gridguide = "";
            ce = "";
            rb = "";
            logo = "none";
            thisSite = new Site();
            within_site = true;
        } else {
            if (qName.equals("id")) {
                within_id = true;
            } else {
                if (qName.equals("name")) {
                    within_name = true;
                } else {
                    if (qName.equals("longname")) {
                        within_longname = true;
                    } else {
                        if (qName.equals("latitude")) {
                            within_latitude = true;
                        } else {
                            if (qName.equals("longitude")) {
                                within_longitude = true;
                            } else {
                                if (qName.equals("ce")) {
                                    within_ce = true;
                                } else {
                                    if (qName.equals("rb")) {
                                        within_rb = true;
                                    } else {
                                        if (qName.equals("logo")) {
                                            within_logo = true;
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

    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("Site")) {
            within_site = false;
            thisSite.addMainInfo(id, name, longname, latitude, longitude, country);
            Sites.put(id, thisSite);
        } else {
            if (qName.equals("id")) {
                within_id = false;
                id = current_value.trim();
            } else {
                if (qName.equals("name")) {
                    within_name = false;
                    name = current_value.trim();
                    if (!within_site) {
                        country = name;
                    }
                } else {
                    if (qName.equals("longname")) {
                        within_longname = false;
                        longname = current_value.trim();
                    } else {
                        if (qName.equals("latitude")) {
                            within_latitude = false;
                            latitude = current_value.trim();
                        } else {
                            if (qName.equals("longitude")) {
                                within_longitude = false;
                                longitude = current_value.trim();
                            } else {
                                if (qName.equals("ce")) {
                                    within_ce = false;
                                    ce = current_value.trim();
                                    thisSite.addCE(ce);
                                } else {
                                    if (qName.equals("rb")) {
                                        within_rb = false;
                                        rb = current_value.trim();
                                        thisSite.addRB(rb);
                                    } else {
                                        if (qName.equals("logo")) {
                                            within_logo = false;
                                            logo = current_value.trim();
                                            thisSite.addLogo(logo);
                                        } else {
                                            if (qName.equals("gridguide")) {
                                                gridguide = current_value.trim();
                                                thisSite.addGridGuide(gridguide);
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

    public Hashtable<String, Site> getSites() {
        return Sites;
    }
}
