/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.layers;

import RTM.RealTimeMonitor;
import RTM.datasource.filetransfer.FileTransferLink;
import RTM.util.StringUtils;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martynia
 */
public class DataTransferTrace {

    Timer timer;
    String layername="FTS Transfer Layer";
    
    public DataTransferTrace(int seconds, int delay) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TraceCreationTask(), delay, //initial delay
                seconds * 1000); //subsequent rate
    }

    class TraceCreationTask extends TimerTask {

        private Hashtable<String, DataTransferSite> fileTransferSites;
        private Hashtable<String, Transfer> transfers;

        @Override
        public void run() {
            try {
                if (RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName(layername).isEnabled()) {
                    fileTransferSites = RealTimeMonitor.getFileTransferSites();
                    for (DataTransferSite value : fileTransferSites.values()) {
                        makeTranfers(value);
                    }
                } else {
                    if(transfers!=null) transfers.clear(); // could be null if ran for the first time ...
                }
            } catch (Exception ex) {
                Logger.getLogger(DataTransferTrace.class.getName()).log(Level.SEVERE, " check if layer " +layername+ " is enabled failed ?", ex);
            }
        }

        private void makeTranfers(DataTransferSite site) {
            // note: site is a WLCG site
            // do INCOMING transfers to 'site' only (an arbitrary decision)
            Transfer transfer = null;
            transfers = RealTimeMonitor.getFileTransfers();
            String id = UUID.randomUUID().toString();
            synchronized (RealTimeMonitor.getFileSyncCollection()) {
                // store transfers separately for every link coming out of each CMS (sub)site of the site:
                for (CMSFileTransferSite subsite : site.getCMSFileTransferSites().values()) {
                    for (FileTransferLink link : subsite.getFromLink().values()) {
                        String from = link.getFrom();
                        if (link.getFrom().endsWith("_Buffer")) {
                            from = StringUtils.chop(link.getFrom(), "_Buffer");
                        } else if (link.getFrom().endsWith("_Disk")) {
                            from = StringUtils.chop(link.getFrom(), "_Disk");
                        }
                        // get a WLCG site name 'from' (a CMS name!) is contained within  
                        String remoteSiteName = "";
                        if (RealTimeMonitor.getFileTransferSitesDB().getReverseSiteMap().containsKey(from)) {
                            remoteSiteName = RealTimeMonitor.getFileTransferSitesDB().getReverseSiteMap().get(from);
                        } else {
                            continue; // no mapping..
                        }
                        DataTransferSite remoteSite = fileTransferSites.get(remoteSiteName);
                        if (remoteSite == null) {
                            continue;
                        }
                        transfer = new Transfer(id, "s",
                                remoteSite.getLatitude(),
                                remoteSite.getLongitude(),
                                site.getLatitude(),
                                site.getLongitude());

                        if (!transfers.containsKey(id)) {
                            transfers.put(id, transfer);
                        }
                    }
                }
            }

        }
    }
}
