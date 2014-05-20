/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.panda.xmlmapadapter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Janusz Martyniak
 */
public class SiteJobs implements Cloneable{

    private int running;
    private int scheduled;
    private int terminatedOK;
    private int terminatedFail;
    private String name = "";

    public SiteJobs() {
    }

    public SiteJobs(String name) {
        this.name = name;
    }
    public SiteJobs(String name, int running, int scheduled, int terminatedOK, int terminatedFail) {
        this.name = name;
        this.running = running;
        this.scheduled = scheduled;
        this.terminatedOK = terminatedOK;
        this.terminatedFail = terminatedFail;
    }

    public String getName() {
        return name;
    }

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public void setRunning(String running) {
        try {
            setRunning(new Integer(running).intValue());
        } catch (java.lang.NumberFormatException ex) {
            Logger.getLogger(SiteJobs.class.getName()).log(Level.SEVERE, " set to 0 ", ex);
            setRunning(0);
        }
    }

    public int getScheduled() {
        return scheduled;
    }

    public void setScheduled(int scheduled) {
        this.scheduled = scheduled;
    }

    public void setScheduled(String scheduled) {
        try {
            setScheduled(new Integer(scheduled).intValue());
        } catch (java.lang.NumberFormatException ex) {
            Logger.getLogger(SiteJobs.class.getName()).log(Level.SEVERE, " set to 0 ", ex);
            setScheduled(0);
        }
    }

    public int getTerminatedFail() {
        return terminatedFail;
    }

    public void setTerminatedFail(int terminatedFail) {
        this.terminatedFail = terminatedFail;
    }

    public int getTerminatedOK() {
        return terminatedOK;
    }

    public void setTerminatedOK(int terminatedOK) {
        this.terminatedOK = terminatedOK;
    }

    public void setTerminatedOK(String terminatedOK) {
        try {
            setTerminatedOK(new Integer(terminatedOK).intValue());
        } catch (java.lang.NumberFormatException ex) {
            Logger.getLogger(SiteJobs.class.getName()).log(Level.SEVERE, " set to 0 ", ex);
            setTerminatedOK(0);
        }
    }

    public void setTerminatedFail(String terminatedFail) {
        try {
            setTerminatedFail(new Integer(terminatedFail).intValue());
        } catch (java.lang.NumberFormatException ex) {
            Logger.getLogger(SiteJobs.class.getName()).log(Level.SEVERE, " set to 0 ", ex);
            setTerminatedFail(0);
        }
    }

    @Override
    public String toString() {
        return "Site:" + name + ":Running=" + running + ":Scheduled=" + scheduled + ":OK=" + terminatedOK + ":FAIL=" + terminatedFail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SiteJobs merge(SiteJobs jobs) {
        if (this.name.equals(jobs.getName())) {
            this.running += jobs.getRunning();
            this.scheduled += jobs.getScheduled();
            this.terminatedFail += jobs.getTerminatedFail();
            this.terminatedOK += jobs.getTerminatedFail();
        } else {
            System.out.println(" illegal attempt to merge 2 SiteJobs objects with different names!");
        }
        return this;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new SiteJobs(name, running, scheduled, terminatedOK, terminatedFail);
    }
}
