/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.filetransfer;

import RTM.RealTimeMonitor;
import RTM.config.Config;
import RTM.layers.DataTransferSite;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This is a main class which reads CMS file transfer data from the RTM Apache server.
 * @author martynia
 */
public class FileTransferUpdatesHandler {
  Timer timer;
  
    public FileTransferUpdatesHandler() {
        this(3600,0);
    }
    public FileTransferUpdatesHandler(int seconds, int delay) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new EventReadingTask(), delay, //initial delay
                seconds*1000); //subsequent rate
    }
    public FileTransferUpdatesHandler(int delay) {
       this(3600, delay);
    }

    public Hashtable<String, DataTransferSite> getSites() {
        return RealTimeMonitor.getFileTransferSites();
    }
    class EventReadingTask extends TimerTask {

        private final String urlBase = Config.getWS_URL() + "/dynamic_information/";
        private String dataString = urlBase + "cms_transfer_data.xml";
        private URL dataURL;

        @Override
        public void run() {

            // parse here
            try {
                dataURL = new URL(dataString);
            } catch (MalformedURLException mue) {
                Logger.getLogger(EventReadingTask.class.getName()).log(Level.SEVERE,"URL problem, exiting File Transfer event reading task");
                return;
            }
            try {

                URLConnection dataURLConnection = dataURL.openConnection();
                if (System.getProperty("rtm.http.proxy") != null) {
                    dataURLConnection.setRequestProperty("Proxy-Authorization", System.getProperty("rtm.http.proxy"));
                }
                BufferedInputStream iStream = new BufferedInputStream(dataURLConnection.getInputStream());
                InputSource source = new InputSource(iStream);

                XMLReader parser = XMLReaderFactory.createXMLReader();
                XMLDataHandler handler = new XMLDataHandler();

                parser.setContentHandler(handler);
                parser.setErrorHandler(handler);

                parser.parse(source);

                iStream.close();
                Logger.getLogger(EventReadingTask.class.getName()).log(Level.FINE," CMS file transfer event reading execution time " + (System.currentTimeMillis() - this.scheduledExecutionTime()));
            } catch (SAXException ex) {
                Logger.getLogger(FileTransferUpdatesHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FileTransferUpdatesHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }  
    public static void main(String [] args){
        FileTransferUpdatesHandler test = new FileTransferUpdatesHandler();
    }
}
