/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.filetransfer;

/**
 *
 * @author martynia
 */
public interface FileTransferLink {
    public FileTransfer getTransfer();
    public String getTo();
    public String getFrom();
}
