/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.filetransfer;

import RTM.RealTimeMonitor;
import RTM.layers.CMSFileTransferSite;
import RTM.layers.DataTransferSite;

import RTM.util.StringUtils;
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
    // <link/>
    private String from = "";
    private String to = "";
    //<transfer/>
    private String binwidth = "";
    private String done_files = "";
    private String timebin = "";
    private String done_bytes = "";
    private String expire_files = "";
    private String rate = "";
    private String fail_bytes = "";
    private String expire_bytes = "";
    private String quality = "";
    private String fail_files = "";
    private CMSFileTransfer transfer;
    private CMSFileTransferLink link;

    public XMLDataHandler() {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        //System.out.println("qName:"+qName.toString()+" local name "+ localName.toString());
        if (localName.equals("phedex")) {
            System.out.println("within phedex)");

        } else if (localName.equals("link")) {
            to = attributes.getValue("to");
            from = attributes.getValue("from");

        } else if (localName.equals("transfer")) {
            binwidth = attributes.getValue("binwidth");
            done_files = attributes.getValue("done_files");
            timebin = attributes.getValue("timebin");
            done_bytes = attributes.getValue("done_bytes");
            expire_files = attributes.getValue("expire_files");
            rate = attributes.getValue("rate");
            fail_bytes = attributes.getValue("fail_bytes");
            expire_bytes = attributes.getValue("expire_bytes");
            quality = attributes.getValue("quality");
            fail_files = attributes.getValue("fail_files");
            Logger.getLogger(XMLDataHandler.class.getName()).log(Level.FINE,"from" + from + " to " + to + " binwidth " + binwidth + " done_files " + done_files + " timebin " + timebin + " done bytes " + done_bytes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (localName.equals("phedex")) {

            System.out.println("done with phedex for site >");
        } else {
            if (localName.equals("link")) {
                link = new CMSFileTransferLink(from, to, transfer);
                //Hashtable<String, DataTransferSite> getFileTransferSites()
                // "to" site, create if necesary, add a "from" link
                if (to.endsWith("_Buffer")) {
                    to = StringUtils.chop(to, "_Buffer");
                } else if (to.endsWith("_Disk")) {
                    to = StringUtils.chop(to, "_Disk");
                }

                if (RealTimeMonitor.getFileTransferSitesDB() == null) {
                    System.out.println("TransferSitesDB is null !");
                }
                if (RealTimeMonitor.getFileTransferSitesDB().getReverseSiteMap() == null) {
                    System.out.println("reverse map is null !");
                }
                // get a DataTransferSite which holds a CMS subsite 'to':
                DataTransferSite tsite = null;
                if (RealTimeMonitor.getFileTransferSitesDB().getReverseSiteMap().containsKey(to)) {
                    tsite = RealTimeMonitor.getFileTransferSites().
                            get((RealTimeMonitor.getFileTransferSitesDB().getReverseSiteMap().get(to)));
                }
                // we don't permit null keys, so:
                if (tsite == null) {
                    return;
                    //tsite = new CMSFileTransferSite();
                    //RealTimeMonitor.getFileTransferSites().put(to, tsite);
                }
                tsite.getCMSFileTransferSites().get(to).addFromLink(link);
                // "from" site ?
                // "to" site, create if necesary, add a "from" link
                if (from.endsWith("_Buffer")) {
                    from = StringUtils.chop(from, "_Buffer");
                } else if (to.endsWith("_Disk")) {
                    from = StringUtils.chop(from, "_Disk");
                }
                //  get a DataTransferSite which holds a CMS subsite 'from':
                DataTransferSite fsite = null;
                if (RealTimeMonitor.getFileTransferSitesDB().getReverseSiteMap().containsKey(from)) {
                    fsite = RealTimeMonitor.getFileTransferSites().
                            get(RealTimeMonitor.getFileTransferSitesDB().getReverseSiteMap().get(from));
                }
                if (fsite == null) {
                    return;
                }
                fsite.getCMSFileTransferSites().get(from).addToLink(link);
            } else if (localName.equals(
                    "transfer")) {
                // define a transfer which will be fed into a CMSFileTransferLink
                transfer = new CMSFileTransfer(binwidth, done_files, timebin,
                        done_bytes, expire_files, rate,
                        fail_bytes, expire_bytes, quality, fail_files);
            }
            current_value = "";
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int len) {
        current_value = current_value.concat(new String(ch, start, len));
    }
}
