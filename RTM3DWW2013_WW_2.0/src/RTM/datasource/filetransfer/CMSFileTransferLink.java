/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.filetransfer;

/**
 *
 * @author Janusz Martyniak
 */
public class CMSFileTransferLink implements FileTransferLink{
    private final String from;
    private final String to;
    private CMSFileTransfer transfer;
    
    public CMSFileTransferLink(String from, String to, CMSFileTransfer transfer){
            this.from=from;
            this.to=to;
            this.transfer=transfer;
    }
    public void setTransfer(CMSFileTransfer transfer) {
        this.transfer=transfer;
    }

    public String getFrom() {
        return from;
    }
    /**
     * Get the original CMS from name (including trailing _Buffer and _Disk)
     * @return 
     */
    public String getCMSFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public CMSFileTransfer getTransfer() {
        return transfer;
    }
    @Override
    public String toString() {
        return "Link from: "+ from + " to: " +to+"\n"+transfer.toString();
    }
    
}
