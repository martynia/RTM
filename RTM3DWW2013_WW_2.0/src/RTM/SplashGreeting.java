// SplashGreeting
package RTM;

import RTM.config.Config;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Calendar;
import java.util.TimeZone;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Splash greeting screen. It also performs initial checks like clock accuracy, network etc. 
 * @author J Martyniak, original design Gidon Moont
 */
class SplashGreeting {

    private static final long serialVersionUID = 15051973L;
    
    private final ImageIcon rtmIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("ui/sidePanel/EST-RTM-LogoFondNoirTr.png")));
    //private final JLabel rtmLabel = new JLabel(rtmIcon);  

    private StartupProgressBar pBar = null;
    private boolean problem = false;
    private JFrame splash;
    private JPanel panel = null;
    private JLabel okLabel = new JLabel();
    private JLabel badLabel = new JLabel();
    private long endEpoch = Calendar.getInstance().getTimeInMillis() + 10100;
    private JButton badButton = new JButton("EXIT");
    private Color backgroundColour = Color.orange;

    //----------------------------------------------------------------------------------------------
    private SplashGreeting() {
        try {
            if(!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    init();
                }
            });
            } else {
                init();
            }
            // invokeAndWait ensures the splash has completed
            // allow the poor user to read the errors ...
            // btw : newer sleep in the dispatch thread (above!), idiot (Bill Clinton)
            if (detectedProblem()) {

                Thread.sleep(10000);
                System.out.println(" timeout exit ");
                System.exit(0);
            }
        } catch (InvocationTargetException ex) {
            System.out.println(" InvocationTargetException " + ex);
        } catch (InterruptedException ex) {
            System.out.println(" Thread sleep failed ? " + ex);
        }
    //--------------------------------------------------------------------------------------------

    }

    private void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        splash = new JFrame("Real Time Monitor startup");
        splash.setBackground(backgroundColour);
        splash.setUndecorated(true);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 5, 2, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;

        panel = new JPanel(gridbag);

        panel.setBackground(backgroundColour);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel rtm_name = new JLabel("3D Real Time Monitor     ", rtmIcon, javax.swing.SwingConstants.CENTER);
        JLabel rtm_version = new JLabel(Config.getVersion(), javax.swing.SwingConstants.CENTER);
        rtm_name.setFont(new Font("SansSerif", Font.PLAIN, 30));
        rtm_version.setFont(new Font("SansSerif", Font.PLAIN, 20));
        rtm_version.setForeground(Color.RED);
        gridbag.setConstraints(rtm_name, c);
        panel.add(rtm_name);
        gridbag.setConstraints(rtm_version, c);
        panel.add(rtm_version);

        String whatsnew = "New in this version:\n" +
                "Atlas Panda job display (Layers->Panda Transfers Layer)\n" +
                "Use a new NASA World Wind 2.0 SDK with JOGL 2\n" +
                "Non fullscreen mode available\nImproved user interaction\n" +
                "External (OS default) browser to display webpages (i.e. GridGuide)\n"+
                "Network Monitoring \n"+
                "CMS Phedex monitoring \n" +
                "Globe animations";
        JTextArea whatsNew = new JTextArea(whatsnew);
        whatsNew.setBackground(Color.orange);
        gridbag.setConstraints(whatsNew, c);
        panel.add(whatsNew);

        try {
            pBar = new StartupProgressBar();
        } catch (Exception ex) {
            Logger.getLogger(SplashGreeting.class.getName()).log(Level.SEVERE, null, ex);
            panel.add(new JLabel(" EDT violation have to quit !"));
            problem = true;
        }
        if (pBar != null) {
            panel.add(pBar);
        // memory check
        }
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        if (maxMemory < 149) {
            JLabel memoryWarning = new JLabel("Your Java Runtime Environment has only " + maxMemory + " MegaBytes (150M required)");
            JLabel memoryInstruction = new JLabel("Please see the README to learn how to increase your memory allocation");
            memoryWarning.setForeground(Color.red);
            memoryInstruction.setForeground(Color.red);
            gridbag.setConstraints(memoryWarning, c);
            panel.add(memoryWarning);
            gridbag.setConstraints(memoryInstruction, c);
            panel.add(memoryInstruction);
            problem = true;
        }

        // connectivity check
        if (!problem) {
            try {
                URL google = new URL("http://www.google.com/");
                URLConnection googleConnection = google.openConnection();
                try {
                    if (System.getProperty("rtm.http.proxy") != null) {
                        googleConnection.setRequestProperty("Proxy-Authorization", System.getProperty("rtm.http.proxy"));
                    }
                } catch (Exception e) {
                }
                googleConnection.connect();
            } catch (IOException ioe) {
                JLabel connectivityWarning = new JLabel("Are you connected?  Even Google is unreachable!...");
                JLabel connectivityInstruction = new JLabel("Please check your wires.  This program requires internet connectivity");
                connectivityWarning.setForeground(Color.red);
                connectivityInstruction.setForeground(Color.red);
                gridbag.setConstraints(connectivityWarning, c);
                panel.add(connectivityWarning);
                gridbag.setConstraints(connectivityInstruction, c);
                panel.add(connectivityInstruction);
                problem = true;
            } catch (Exception e) {
            }
        }

        // time check - if local computer is > 3 minutes in the future then no transfers will show
        // we are not going to allow more than 10 seconds of drift anyways...
        if (!problem) {

            long delta = 100000001;
            long difference = 0;
            try {
                URL epoch = new URL(Config.getWS_URL()+"/cgi-bin/epoch_time.cgi");
                URLConnection epochConnection = epoch.openConnection();
                try {
                    if (System.getProperty("rtm.http.proxy") != null) {
                        epochConnection.setRequestProperty("Proxy-Authorization", System.getProperty("rtm.http.proxy"));
                    }
                } catch (Exception e) {
                }
                epochConnection.connect();
                BufferedReader buff = new BufferedReader(new InputStreamReader(epochConnection.getInputStream()));
                String epochString = buff.readLine();
                long epochCorrect = Long.parseLong(epochString);
                long epochLocal = Calendar.getInstance().getTimeInMillis();
                difference = epochLocal - epochCorrect;
                delta = difference * difference;
                difference = difference / 1000L;
                if (difference > 3L) {
                    System.out.println("Your computer is fast by approx. " + difference + " seconds");
                } else {
                    if (difference < -3L) {
                        System.out.println("Your computer is slow by approx. " + difference + " seconds");
                    }
                }
            } catch (IOException ioe) {
                System.out.println("could not get hold of the pseudo ntp cgi page?");
                problem = true;
            }

            if (delta > 300000000L) {
                String warningMessage = "Could not establish if your computer's clock is correct???  This may cause problems";
                if (difference > 30L) {
                    warningMessage = new String("Your computer is fast by approx. " + difference + " seconds.  This would cause problems");
                } else {
                    if (difference < -30L) {
                        warningMessage = new String("Your computer is slow by approx. " + difference + " seconds.  This would cause problems");
                    }
                }
                JLabel clockWarning = new JLabel(warningMessage);
                JLabel clockInstruction = new JLabel("Please synchronize your computer's clock and try again");
                clockWarning.setForeground(Color.red);
                clockInstruction.setForeground(Color.red);
                gridbag.setConstraints(clockWarning, c);
                panel.add(clockWarning);
                gridbag.setConstraints(clockInstruction, c);
                panel.add(clockInstruction);
                //problem = true;
                problem=false;
            }
        }

        if (problem) {

            endEpoch = Calendar.getInstance().getTimeInMillis() + 3600000;

            badLabel.setText("Click to exit");
            c.gridwidth = 1;
            gridbag.setConstraints(badLabel, c);
            panel.add(badLabel);

            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(badButton, c);
            badButtonHandler();
            panel.add(badButton);

        }

        splash.add(panel);
        splash.pack();
        Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
        splash.setLocation((sd.width - splash.getWidth()) / 2, (sd.height - splash.getHeight()) / 2);
        splash.setAlwaysOnTop(true);
        splash.setVisible(true);
    }

    //----------------------------------------------------------------------------------------------
    /** 
     * Add a listener to the EXIT button. 
     */
    public void badButtonHandler() {

        // Action Listeners
        badButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                splash.setVisible(false);
                // odd to try and make Applets crash asap without doing anything more - JVM will still run...
                try {
                    System.exit(0);
                } catch (Exception ex) {
                }
            }
        });

    }

    //----------------------------------------------------------------------------------------------
    /**
     * Returns the progress bar handle to allow collers to interact with it.
     * @return StartupProgressBar reference
     */
    public StartupProgressBar getProgressBarPanel() {
        return pBar;
    }

    /**
     * True if a problem occurs during the initialisation phase
     * @return true if a problem occured, false otherwise
     */
    public boolean detectedProblem() {
        return problem;
    }

    public void dispose(long milis) {
        try {
            Thread.sleep(milis); // leave the splash on before disposing
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    if (splash != null) {
                        splash.dispose();
                        splash = null;
                    }
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(SplashGreeting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Named constructor.
     * @return SplashGreeting instance
     */
    public static synchronized SplashGreeting getInstance() {
        if (instance == null) {
            instance = new SplashGreeting();
        }
        return instance;
    }
    private static SplashGreeting instance = null;
    //----------------------------------------------------------------------------------------------
}
