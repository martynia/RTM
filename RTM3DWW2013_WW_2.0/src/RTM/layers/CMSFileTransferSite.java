/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.layers;

import RTM.datasource.filetransfer.CMSFileTransferLink;
import RTM.datasource.filetransfer.FileTransferLink;
import java.util.HashMap;

/**
 * The class represents a CMS transfer site, with a CMS specific name and in/out links
 * @author Janusz Martynia
 */
public class CMSFileTransferSite {
    
    public CMSFileTransferSite(String name) {
       cmsName=name;
    }
     
    public HashMap<String, FileTransferLink> getFromLink() {
        return fromLink;
    }

    public HashMap<String, FileTransferLink> getToLink() {
        return toLink;
    }

    public void addFromLink(FileTransferLink link) {
        fromLink.put(link.getFrom(), link);
    }

    public void addToLink(FileTransferLink link) {
        toLink.put(link.getFrom(), link);
    }
    public String getName() {
        return cmsName;
    }
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Object value : fromLink.values()) {
            buf.append(value.toString());
        }
        buf.append("\n");
        for (Object value : toLink.values()) {
            buf.append(value.toString());
        }
        buf.append("\n");
        return buf.toString();
    }
    protected HashMap<String, FileTransferLink> fromLink = new HashMap<String, FileTransferLink>();
    protected HashMap<String, FileTransferLink> toLink = new HashMap<String, FileTransferLink>();
    protected String cmsName;
}
