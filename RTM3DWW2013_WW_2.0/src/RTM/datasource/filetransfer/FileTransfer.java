/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.filetransfer;

/**
 *
 * @author martynia
 */
public interface  FileTransfer {
    public double getFquality();
    public double getFrate();
    public int getIbinwidth();
    public long getIdone_bytes();
    public int getIdone_files();
    public long getIexpire_bytes();
    public int getIexpire_files();
    public long getIfail_bytes();
    public int getIfail_files();
    public long getItimebin();   
}
