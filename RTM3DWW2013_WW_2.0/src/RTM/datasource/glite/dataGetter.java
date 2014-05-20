// Client
package RTM.datasource.glite;

import RTM.RealTimeMonitor;
import RTM.config.Config;
import java.util.Calendar;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.xml.sax.InputSource;
// changelog
// JM 22.10.2009  set not to use caches on the URL connection. Important for applets.
/**
 * Data handling thread. Read data from the Apache Web area.
 * @author Gidon Moont, adapted by Misha and JM
 */
public class dataGetter implements Runnable {

    private boolean this_thread_ok = true;
    private final String urlBase = Config.getWS_URL()+"/dynamic_information/";
    private URL dataURL = null;

    public dataGetter() {

        String dataString = urlBase + "all-rtm.xml.gz";
        try {
            dataURL = new URL(dataString);
        } catch (MalformedURLException mue) {
            System.out.println("URL problem");
            this_thread_ok = false;
        }
        System.out.println("Started dataGetter");

        // do first loop on total data
        try {
            long start_processing = Calendar.getInstance().getTimeInMillis();

            URLConnection dataURLConnection = dataURL.openConnection();
            if (System.getProperty("rtm.http.proxy") != null) {
                dataURLConnection.setRequestProperty("Proxy-Authorization", System.getProperty("rtm.http.proxy"));
            }
            GZIPInputStream gzippedStream = new GZIPInputStream(dataURLConnection.getInputStream());
            InputSource source = new InputSource(gzippedStream);

            XMLReader parser = XMLReaderFactory.createXMLReader();
            XMLDataHandler handler = new XMLDataHandler();

            parser.setContentHandler(handler);
            parser.setErrorHandler(handler);

            parser.parse(source);

            gzippedStream.close();

            long processing = Calendar.getInstance().getTimeInMillis() - start_processing;

            int size = 0;
            synchronized (RealTimeMonitor.getEvents()) {
                size = RealTimeMonitor.getEvents().size();
            }
            // do nothing more here. Events are handled by the EventHandler class
            System.out.println("\ninitial read of all inclusive  file\tevents => " + handler.getEventsCount() + " [ " + handler.getEarlyEventsCount() + " / " + handler.getLateEventsCount() + " ]" + "\ttime taken to process => " + processing + "ms\teventsLeft => " + size);
            System.out.println("\ninitial jobs => " + handler.getJobsCount());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setThreadStop() {
        this_thread_ok = false;
    }

    public void run() {

        // reset URL to updates file now - this will be read immediately - so we will not miss anything
        String dataString = urlBase + "updates-rtm.xml.gz";
        try {
            dataURL = new URL(dataString);
            this_thread_ok = true; //JM 11 Sep 2013
        } catch (MalformedURLException mue) {
            System.out.println("URL problem");
            this_thread_ok = false;
        }

        System.out.println("Subsequent updates");

        // everlasting loop
        String last_updatenumber = "0";

        while (this_thread_ok) {

            try {
                long start_processing = Calendar.getInstance().getTimeInMillis();

                URLConnection dataURLConnection = dataURL.openConnection();
                dataURLConnection.setUseCaches( false ) ; // JM on 22.10.2009 - vital for applets, otherwise
                                                          // update number is not incremented
                if (System.getProperty("rtm.http.proxy") != null) {
                    dataURLConnection.setRequestProperty("Proxy-Authorization", System.getProperty("rtm.http.proxy"));
                }
                GZIPInputStream gzippedStream = new GZIPInputStream(dataURLConnection.getInputStream());
                InputSource source = new InputSource(gzippedStream);

                XMLReader parser = XMLReaderFactory.createXMLReader();
                XMLUpdatesHandler handler = new XMLUpdatesHandler(last_updatenumber);

                parser.setContentHandler(handler);
                parser.setErrorHandler(handler);

                parser.parse(source);

                gzippedStream.close();

                long processing = Calendar.getInstance().getTimeInMillis() - start_processing;

                int size = 0;
                synchronized (RealTimeMonitor.getEvents()) {
                    size = RealTimeMonitor.getEvents().size();
                }

                last_updatenumber = handler.getUpdateNumber();
                long currentMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            //System.out.println("Max memory = " + currentMemory + " MegaBytes");
                // currently no early events, so:
                System.out.println("\nupdate number => " + handler.getUpdateNumber() + "\tMem=> " + currentMemory +"MB"+ "\tevents this cycle=> " + handler.getEventsCount() + "\tlate (older than 3 mins)=> " + handler.getLateEventsCount() + "\tprocessing => " + processing + "ms\teventsLeft in buffer => " + size);

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
        Logger.getLogger(dataGetter.class.getName()).warning("gLita data handler exited, no more events will be read ");
    }
}
