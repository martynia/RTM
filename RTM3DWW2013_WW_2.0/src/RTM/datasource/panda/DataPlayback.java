// Client
package RTM.datasource.panda;

import RTM.RealTimeMonitor;
import RTM.config.Config;
import RTM.playback.PlaybackFactory;
import RTM.playback.RingList;
import java.util.Calendar;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Data handling for Panda playback thread. Read data from a local playback file.
 * This class is not Runnable, because it uses the PandaUpdatesHandlerClass which
 * is a Timer. Create an object and call run().
 * @author Janusz Martyniak
 */
public class DataPlayback {

    private boolean this_thread_ok = true;
    private final String urlBase = Config.getWS_URL() + "/dynamic_information/";
    private URL dataURL = null;
    private RingList<String> pplist = null;
    private PandaUpdatesHandler puh=null;
    
    public DataPlayback() {

        try {
            Class.forName("RTM.playback.PandaPlayback");
            //Class.forName("RTM.playback.PandaPlayback");
            pplist = (RingList<String>) PlaybackFactory.getInstance().createPlaybackList("Panda").getRingList();
            System.out.println(" ######### panda playback############ size \n" + pplist.size());
            System.out.println(" ######### panda playback############\n" + pplist.toString());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RealTimeMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setThreadStop() {
        this_thread_ok = false;
        if(puh != null) {
            puh.stop();
        }
    }

    public void run() {

        // get the playback file
        int num_bad = 0;

        if (pplist == null || pplist.size() == 0) {
            Logger.getLogger(DataPlayback.class.getName()).log(Level.SEVERE, " No Panda playback file list found or empty ");
            return;
        }
        // run over a RingList
        String last_updatenumber = "0";

        for (String dataString : pplist) { // this is an infinite loop
            // get the playback file
            //String dataString = urlBase + "playback-rtm.xml.gz";
            try {
                dataURL = new URL(dataString);
                System.out.println(" Playback, file read !");
            } catch (MalformedURLException mue) {
                System.out.println("Playback URL problem" + dataString);
                num_bad++;

                if (num_bad > pplist.size()) {
                    this_thread_ok = false;
                    return;
                }
                continue;
            }
            // if a thread is stopped externally, accept the request
            if (!this_thread_ok) {
                return;
            }

            // loop over files in the playback file list. One file is one Panda snapshot
            // in real time run it corresponds to one set of traces, normally completed
            // withing a minute. Here we have a different approach. Ones parsed we have got the current
            // snapshot, so we feed it into the data structure and go to the next playback file.

            long start_processing = Calendar.getInstance().getTimeInMillis();
            //puh = new PandaUpdatesHandler(600, 0, dataURL.toString();
        }

    }
}
