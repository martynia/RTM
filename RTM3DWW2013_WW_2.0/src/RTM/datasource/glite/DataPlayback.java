// Client
package RTM.datasource.glite;

import RTM.RealTimeMonitor;
import RTM.config.Config;
import RTM.playback.GlitePlaybackList;
import RTM.playback.PlaybackFactory;
import java.util.Calendar;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.xml.sax.InputSource;
/**
 * Data handling thread. Read data from the Apache Web area. For playback ONLY
 * @author Janusz Martyniak
 */
public class DataPlayback implements Runnable {

    private boolean this_thread_ok = true;
    private final String urlBase = Config.getWS_URL() + "/dynamic_information/";
    private URL dataURL = null;
    private GlitePlaybackList<String> gplist = null;

    public DataPlayback() {
        try {
            Class.forName("RTM.playback.GlitePlaybackList");
            //Class.forName("RTM.playback.PandaPlaybackList");
            gplist = (GlitePlaybackList<String>) PlaybackFactory.getInstance().createPlaybackList("gLite");
            System.out.println(" ######### glite playback############ size \n" + gplist.size());
            System.out.println(" ######### glite playback############\n" + gplist.toString());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RealTimeMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setThreadStop() {
        this_thread_ok = false;
        Logger.getLogger(DataPlayback.class.getName()).warning("Stopping data playback ... no more data will be read from a playback file");
    }

    public void run() {
        // be optimistic:
        int num_bad = 0;
        this_thread_ok = true;
        // get out of here if list has zero length
        if (gplist == null || gplist.size() == 0) {
            Logger.getLogger(DataPlayback.class.getName()).log(Level.SEVERE, " No playback file list found or empty ");
            return;
        }
        // run over a RingList
        String last_updatenumber = "0";

        for (String dataString : gplist) { // this is an infinite loop
            // get the playback file
           
            try {
                dataURL = new URL(dataString);
                System.out.println(" Playback, file read !");
            } catch (MalformedURLException mue) {
                System.out.println("Playback URL problem"+dataString);
                num_bad++;
                
                if (num_bad > gplist.size()) {
                    this_thread_ok = false;
                    return;
                }
                continue;
            }
            // if a thread is stopped externally, accept the request
            if (!this_thread_ok) {
                return;
            }

            // loop over files in the playback file list. One file is one gLite DB snapshot
            // in real time run it corresponds to one set of traces, normally completed
            // withing a minute. Here we have a different approach. Ones parsed we have got the current
            // sapshot, so we feed it into the data structure and go to the next playback file.
            //while (this_thread_ok) {

            try {
                long start_processing = Calendar.getInstance().getTimeInMillis();

                URLConnection dataURLConnection = dataURL.openConnection();
                dataURLConnection.setUseCaches(false); // JM on 22.10.2009 - vital for applets, otherwise
                // update number is not incremented
                GZIPInputStream gzippedStream = new GZIPInputStream(dataURLConnection.getInputStream());
                InputSource source = new InputSource(gzippedStream);

                XMLReader parser = XMLReaderFactory.createXMLReader();
                XMLUpdatesHandler handler = new XMLUpdatesHandler(last_updatenumber);
                // set initial timestamp for playback
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, -3);
                handler.setPlaybackTimestamp(new Timestamp(calendar.getTimeInMillis()));

                parser.setContentHandler(handler);
                parser.setErrorHandler(handler);

                parser.parse(source);

                gzippedStream.close();

                long processing = Calendar.getInstance().getTimeInMillis() - start_processing;

                int size = 0;
                synchronized (RealTimeMonitor.getEvents()) {
                    size = RealTimeMonitor.getEvents().size();
                }

                //last_updatenumber = handler.getUpdateNumber();
                last_updatenumber = Integer.toString(Integer.parseInt(last_updatenumber) + 1); // for now
                long currentMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
                //System.out.println("Max memory = " + currentMemory + " MegaBytes");
                // currently no early events, so:
                System.out.println("\nPlayback update number => " + handler.getUpdateNumber() + "\tMem=> " + currentMemory + "MB" + "\tevents this cycle=> " + handler.getEventsCount() + "\tlate (older than 3 mins)=> " + handler.getLateEventsCount() + "\tprocessing => " + processing + "ms\teventsLeft in buffer => " + size);

            } catch (Exception e) {
                e.printStackTrace();
            }

            //---------------------------------------------------------------------------------------------

            Calendar waiter = Calendar.getInstance();
            int milliseconds_to_minute = (90 - waiter.get(Calendar.SECOND)) * 1000;
            milliseconds_to_minute = (milliseconds_to_minute < 60001) ? milliseconds_to_minute : milliseconds_to_minute - 60000;
            // wait
            try {
                Thread.sleep(milliseconds_to_minute);
            } catch (InterruptedException e) {
                System.out.println("sleep failed?");
                System.out.println(e);
                this_thread_ok = false;
            }




            //---------------------------------------------------------------------------------------------

        }
//end for
        Logger.getLogger(DataPlayback.class.getName()).log(Level.INFO, " Playback terminated by request ");
    }
}
