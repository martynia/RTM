/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM;

import RTM.config.Config;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.util.WWUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import javax.swing.JApplet;

/**
 *
 * @author Janusz Martyniak
 */
public class RTMApplet extends JApplet {

    private static final long serialVersionUID = 426204280515L;

    public RTMApplet() {

        //Adjust application clock to the server clock
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println("Current time is: "
                + timeFormat.format(Calendar.getInstance().getTimeInMillis())
                + " (local)");
    }

    public void init() {
        System.out.println(" The applet is loaded and will start shortly ...");
        Config.setApplet(true);
    }

    public void start() {
        try {
            //
            RTMApplicationDesktop jdp = new RTMApplicationDesktop();
            this.setContentPane(jdp);
            WWUtil.alignComponent(null, this, AVKey.CENTER);
            java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    RTMApplet.this.setVisible(true);
                    RealTimeMonitor.showAboutIFrame();
                }
            });
            jdp.getWwd().requestFocusInWindow();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    private final String appName = "GRID RTM3D WW 09 Applet (Beta)";
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
}
