/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.panda;

import RTM.datasource.panda.xmlmapadapter.SiteJobs;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Janusz Martyniak
 * 
 */
public class JobStatisticsHandler {

    private JobStatisticsHandler() {
        statMap = new HashMap<String, SiteJobs>();
    }

    /**
     * Store a map of SiteeJobs. The site name is the key.
     * @param originalMap 
     */
    void put(HashMap<String, SiteJobs> originalMap) {
        // we need a deep clone of values:

        for (Map.Entry<String, SiteJobs> entry : originalMap.entrySet()) {
            try {
                statMap.put(entry.getKey(), (SiteJobs) entry.getValue().clone());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(JobStatisticsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Return number of running Panda jobs for a given siteName.
     * @param siteName Name of the site.
     * @return number of running jobs jobs.
     */
    public int getRunning(String siteName) {

        if (statMap.containsKey(siteName)) {
            return Math.abs(statMap.get(siteName).getRunning());
        } else {
            return -1;
        }
    }

    /**
     * Return number of scheduled Panda jobs for a given siteName.
     * @param siteName Name of the site
     * @return number of scheduled jobs
     */
    public int getScheduled(String siteName) {
        if (statMap.containsKey(siteName)) {
            return Math.abs(statMap.get(siteName).getScheduled());
        } else {
            return -1;
        }
    }

    /**
     * Named constructor.
     * @return a unique JobStatisticsHandler instance.
     */
    public static synchronized JobStatisticsHandler getInstance() {
        if (instance == null) {
            instance = new JobStatisticsHandler();
        }
        return instance;
    }
    private static HashMap<String, SiteJobs> statMap;
    private static JobStatisticsHandler instance;
}
