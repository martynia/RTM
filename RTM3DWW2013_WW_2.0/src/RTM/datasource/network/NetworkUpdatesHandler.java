/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.network;

import RTM.config.Config;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Network monitoring events handler. Gets network data from the RTM Apache server
 * and periodically update the network sites instances.
 * @author martynia
 */
public class NetworkUpdatesHandler {

    Timer timer;

    public NetworkUpdatesHandler(int seconds, int delay) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new EventReadingTask(), delay, //initial delay
                seconds*1000); //subsequent rate
    }

    class EventReadingTask extends TimerTask {

        private final String urlBase = Config.getWS_URL() + "/dynamic_information/";
        private String dataString = urlBase + "network_data_v3.xml";
        private URL dataURL;

        @Override
        public void run() {

            // parse here
            try {
                dataURL = new URL(dataString);
            } catch (MalformedURLException mue) {
                System.out.println("URL problem, exiting Network event reading task");
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
                System.out.println(" Network event reading execution time " + (System.currentTimeMillis() - this.scheduledExecutionTime()));
            } catch (SAXException ex) {
                Logger.getLogger(NetworkUpdatesHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(NetworkUpdatesHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
