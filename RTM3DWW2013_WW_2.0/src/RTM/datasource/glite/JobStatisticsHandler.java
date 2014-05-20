/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.glite;

import RTM.RealTimeMonitor;
import RTM.config.Config;
import RTM.jaxb.jobstat.Ce;
import RTM.jaxb.jobstat.State;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

/**
 * Class to get job statistics data from the server. Run as a thread in an infinite loop.
 * @author Janusz Martyniak
 */
public class JobStatisticsHandler implements Runnable {
    private String rbChoices;
    private String voChoices;

    /**
     * Create JobStatisticsHandler with default webroot defined by RealTimeMonitor Config class.
     */
    public JobStatisticsHandler() {
        String dataString = urlBase + "updates-stats-rtm.xml";
        try {
            dataURL = new URL(dataString);
            u = new JobStatUnmarshaller(dataURL);

        } catch (JAXBException ex) {
            Logger.getLogger(JobStatisticsHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JobStatisticsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Thread run method. Runs in an infinite loop, periodically getting CE statistics.
     * The CE statistics reader is itself a (timeout 15s) thread.
     */
    public void run() {
        if (dataURL != null && u != null) {
            try {
                while (true) {
                    // unmarshaller returns a syc-ed list.
                    CElist = u.getCEList();
                    computeGlobalStats();
                    RealTimeMonitor.reChooseSites(); // update stats at sites
                    Thread.sleep(60000);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(JobStatisticsHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JAXBException ex) {
                Logger.getLogger(JobStatisticsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * Test method to get global Running/Scheduled values.
     * @throws JAXBException
     */
    public void computeGlobalStats() throws JAXBException {

        Ce ce;
        // these are RB?VO choices as requsted by user (via RB/VO screens)
        rbChoices = RealTimeMonitor.getRBList();
        voChoices = RealTimeMonitor.getVOList();
        int scheduled = 0;
        int running =0;
        for (Iterator iter = CElist.iterator(); iter.hasNext();) {
            ce = (Ce) iter.next();
            if (isChosen(ce)) {
                running += ((State) ce.getState()).getRunning().intValue();
                scheduled += ((State) ce.getState()).getScheduled().intValue();
            }
        }
        System.out.println(" Running " + running + " Scheduled " + scheduled + "(list size: "+ CElist.size()+")");
    }
    /**
     * Return a synchronized list of CE statistics objects.
     * @return CE list (might be empty, but never null)
     */
    public static List<Object> getSynchronizedCElist() {
        return CElist;
    }
    /**
     * Check if a given RB or VO is chosen (by user) for a corresponding CE.
     * @param ce
     * @return
     */
    private boolean isChosen(Ce ce) {
        boolean chosen = false;
                if (rbChoices.equals("all")) {
                    chosen = true;
                } else {
                    String thisRB = "-" + ce.getRb() + "-";
                    if (rbChoices.indexOf(thisRB) > 0) {
                        chosen = true;
                    }
                }
                if (chosen) {
                    if (!voChoices.equals("all")) {
                        String thisVO = "-" + ce.getVo() + "-";
                        if (!(voChoices.indexOf(thisVO) > 0)) {
                            chosen = false;
                        }
                    }
                }
        return chosen;
    }
    private final String urlBase = Config.getWS_URL()+"/dynamic_information/";
    private URL dataURL = null;
    private static List<Object> CElist;
    private JobStatUnmarshaller u;
}
