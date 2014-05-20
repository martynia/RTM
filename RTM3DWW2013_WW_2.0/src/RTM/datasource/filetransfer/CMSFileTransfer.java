/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.filetransfer;

import java.util.Date;

/**
 *
 * @author martynia
 */
public class CMSFileTransfer implements FileTransfer{

    
    private  int ibinwidth;
    private  int idone_files;
    private  long itimebin;
    private  long idone_bytes;
    private  int iexpire_files;
    private  double frate;
    private  long ifail_bytes;
    private  long iexpire_bytes;
    private  double fquality;
    private  int ifail_files;
    
    public CMSFileTransfer(String binw, String done_files, String timebin, 
                           String done_bytes, String expire_files, String rate,
                           String fail_bytes, String expire_bytes, String quality, String fail_files) {
        
        ibinwidth = Integer.parseInt(binw);
        idone_files = Integer.parseInt(done_files);
        itimebin = Long.parseLong(timebin);
        idone_bytes = Long.parseLong(done_bytes);
        iexpire_files = Integer.parseInt(expire_files);
        frate= Double.parseDouble(rate);
        ifail_bytes = Long.parseLong(fail_bytes);
        iexpire_bytes = Long.parseLong(expire_bytes);
        try {
          fquality= Double.parseDouble(quality);
        }
        catch(java.lang.NumberFormatException fex) {
          fquality=0;   
        }
        ifail_files = Integer.parseInt(fail_files);
    }
    public double getFquality() {
        return fquality;
    }

    public double getFrate() {
        return frate;
    }

    public int getIbinwidth() {
        return ibinwidth;
    }

    public long getIdone_bytes() {
        return idone_bytes;
    }

    public int getIdone_files() {
        return idone_files;
    }

    public long getIexpire_bytes() {
        return iexpire_bytes;
    }

    public int getIexpire_files() {
        return iexpire_files;
    }

    public long getIfail_bytes() {
        return ifail_bytes;
    }

    public int getIfail_files() {
        return ifail_files;
    }

    public long getItimebin() {
        return itimebin;
    }
    public String toString() {
        return new Date(itimebin*1000).toString()+ " time frame (s): "+ibinwidth+" Done files: "+idone_files+" Rate: "+frate/1000+"kb/s\n";
    }
}
