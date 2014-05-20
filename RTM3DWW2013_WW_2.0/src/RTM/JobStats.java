/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM;

import RTM.layers.Site;
import java.util.Observable;

/**
 * Job Statistics Observable. It acquires global job numbers for running and scheduled jobs and
 * periodically notifies the observer (the job counter on the screen) about current values.
 * @author Janusz Martyniak
 */
class JobStats extends Observable implements Runnable {

    JobStats(Iterable<Site> sites) {
        this.isites = sites;
        for (Site s : sites) {
            runningCE += s.getRunningCE();
            scheduledCE += s.getScheduledCE();
        }
    }

    public void run() {
        while (OK) {
            runningCE=scheduledCE=0;
            for (Site s : isites) {
                runningCE += s.getRunningCE();
                scheduledCE += s.getScheduledCE();
                setChanged();
                notifyObservers("Running " + runningCE + ", Scheduled " + scheduledCE );
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("sleep failed?");
            }
        }
    }
    
    private int runningCE = 0, scheduledCE = 0;
    private Iterable<Site> isites;
    private Boolean OK = true;
}
