/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.ping;

import RTM.config.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contact the data webserver and report client-side OS, Java versions and
 * JVM memory usage
 * @author Janusz Martyniak
 */
public class WebserverPing implements Runnable {
    /**
     * Get system information from Java properties.
     */
    public WebserverPing(){
      String javaVersion = System.getProperty("java.version");
      String javaVendor = System.getProperty("java.vendor");
      String osName = System.getProperty("os.name");
      String osVersion = System.getProperty("os.version");
      String osArch = System.getProperty("os.arch");
      info=javaVersion+" "+javaVendor+" "+osName+" "+osVersion+" "+osArch;
      System.out.println(info);
      start =Calendar.getInstance().getTimeInMillis();
    }
    /**
     * Store system properties at the server side log every 10 minutes.
     */
    public void run() {
        long currentMemory;
        while(true) {
            try {
                currentMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
                URL reply = new URL(Config.getWS_URL() + "/cgi-bin/rtm_ping.perl?info="+URLEncoder.encode(info,"UTF-8"));
                info = Long.toString(currentMemory);
                URLConnection epochConnection = reply.openConnection();
                try {
                    if (System.getProperty("rtm.http.proxy") != null) {
                        epochConnection.setRequestProperty("Proxy-Authorization", System.getProperty("rtm.http.proxy"));
                    }
                } catch (Exception e) {
                }
                epochConnection.connect();
                BufferedReader buff = new BufferedReader(new InputStreamReader(epochConnection.getInputStream()));
                String replyString = buff.readLine();
                Logger.getLogger(WebserverPing.class.getName()).log(Level.INFO," Webserver replied " + replyString );
                Thread.sleep(600000);
                now = Calendar.getInstance().getTimeInMillis();
                //info = "up "+String.format("%10.1f",(now-start)/60000.) + "min";
                Date date = new Date(now-start);
                info = "up "+String.format("%3d days ", (now-start)/(24*3600000))+format.format(date) + "min" + " Mem " + currentMemory+"MB";
            } catch (InterruptedException ex) {
                Logger.getLogger(WebserverPing.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WebserverPing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
private String info;
private long start = 0, now=0;
private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
}
