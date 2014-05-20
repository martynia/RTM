/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.panda;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Vector;
import RTM.dboard.util.WeightedRandomGenerator;
import RTM.datasource.panda.xmlmapadapter.Jobsummary;
import RTM.datasource.panda.xmlmapadapter.SiteJobs;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Janusz Martyniak
 */
public class PandaJobFactory {

    public PandaJob createJob(Jobsummary jobsummary) {
        String id = "Panda";
        String rb = null;
        String state = null;
        String ce = null;
        String queue = null;
        String ui = null;
        String vo = null;

        String registered = null;
        String update = null;
        String rtm_timestamp = null;

        GregorianCalendar start = jobsummary.getStartdatetime().toGregorianCalendar();
        GregorianCalendar end = jobsummary.getEnddatetime().toGregorianCalendar();

        long val_1 = start.getTimeInMillis();
        long val_2 = end.getTimeInMillis();

        //random date between the 2

        Random r = new Random();
        long randomTS = (long) (r.nextDouble() * (val_2 - val_1)) + val_1;
        Date d = new Date(randomTS);

        // SiteJobs

        // pick a site
        Vector<String> v = new Vector<String>();
        //List<String> v;
        Vector<Integer> w = new Vector<Integer>();
        SiteJobs jobs;
        String siteName;
        jobsummary.getSiteJobList().getMap().remove("unknown");
        do {
            Random rand = new Random();
            // have to re-do this after the Map/Set/List shrinks
            String[] siteJobsNames = new String[jobsummary.getSiteJobList().getMap().size()];
            if (siteJobsNames.length == 0) {
                return null;
            }
            siteJobsNames = jobsummary.getSiteJobList().getMap().keySet().toArray(siteJobsNames);
            siteName = siteJobsNames[rand.nextInt(siteJobsNames.length)];
            // get jobs for the site
            jobs = (SiteJobs) jobsummary.getSiteJobList().getMap().get(siteName);

            String[] jobtypes = {"Scheduled", "Done", "Aborted"};
            //v = Arrays.asList("Scheduled", "Done", "Aborted");
            Collections.addAll(v, jobtypes);
            // all urrent weights
            int allWeights[] = {Math.abs(jobs.getScheduled()),
                Math.abs(jobs.getTerminatedOK()),
                Math.abs(jobs.getTerminatedFail()) //jobs.getRunning()
            };
            // strip zeros off

            for (int i = 0, k = 0; i < allWeights.length; i++, k++) {
                if (allWeights[i] > 0) {
                    w.add(allWeights[i]);
                } else {
                    v.remove(k);
                    k--;
                }
            }
            //System.out.println(" Jobtype vector size " + v.size());
            if (v.size() == 0) {
                jobsummary.getSiteJobList().getMap().remove(siteName);
            }
        } while (v.size() == 0);
        int[] weights = new int[w.size()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = w.get(i).intValue();
            //System.out.println("site: " + siteName + ", jobtype " + v.get(i) + " #jobs " + weights[i]);
            //System.out.flush();
        }
        // pick one job from a non zero collection.
        WeightedRandomGenerator wrng = new WeightedRandomGenerator(weights);
        int result = wrng.next();
        //System.out.println(" jobtypes size " + v.size() + " weights size " + weights.length + " result " + result);
        if (v.get(result).equals("Scheduled")) {
            jobs.setScheduled(Math.abs(jobs.getScheduled()) - 1);
            //return new PandaJob(id, "Scheduled", "CERN-PROD", siteName, randomTS, vo);
        } else if (v.get(result).equals("Done")) {
            jobs.setTerminatedOK(Math.abs(jobs.getTerminatedOK()) - 1);
            //return new PandaJob(id, "Done", siteName, "CERN-PROD", randomTS, vo);
        } else if (v.get(result).equals("Aborted")) {
            jobs.setTerminatedFail(Math.abs(jobs.getTerminatedFail()) - 1);
            //return new PandaJob(id, "Aborted", siteName, "CERN-PROD", randomTS, vo);
        } else if (v.get(result).equals("Running")) {
            jobs.setRunning(Math.abs(jobs.getRunning()) - 1);
        } else {
            return null;
        }
        return new PandaJob(id, v.get(result), "CERN-PROD", siteName, randomTS, vo);

    }
}
