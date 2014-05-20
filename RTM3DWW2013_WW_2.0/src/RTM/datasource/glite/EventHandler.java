// EventHandler
package RTM.datasource.glite;

import RTM.O;
import RTM.RealTimeMonitor;
import RTM.datasource.panda.PandaJob;
import RTM.job.GenericJob;
import RTM.layers.Site;
import RTM.layers.Transfer;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Get events (glite and Panda) from the events map and fill the transfers in.
 */


public class EventHandler implements Runnable {

    private boolean this_thread_ok = true;
    private Hashtable<String, String> IPLookup = new Hashtable<String, String>();
    private Timestamp nextEvent = null;
    private GenericJob thisJob = null;
    private Transfer thisTransfer = null;
    private boolean waitforevents = false;

    public EventHandler() {

        // populate IPLookup Hashtable (ip to site id)
        for (Enumeration S = RealTimeMonitor.getSites().elements(); S.hasMoreElements();) {
            Site thisSite = (Site) S.nextElement();
            String id = thisSite.getID();
            ArrayList<String> CEs = thisSite.getCEs();
            Iterator iterateCEs = CEs.iterator();
            while (iterateCEs.hasNext()) {
                String ip = (String) iterateCEs.next();
                IPLookup.put(ip, id);
            }
            ArrayList<String> RBs = thisSite.getRBs();
            Iterator iterateRBs = RBs.iterator();
            while (iterateRBs.hasNext()) {
                String ip = (String) iterateRBs.next();
                IPLookup.put(ip, id);
            }
        }
        System.out.println("Started EventHandler");
    }

    public void setThreadStop() {
        this_thread_ok = false;
    }

    public void run() {
        // todo handle properly (skip) certain actions when gLite/Panda disabled
        while (this_thread_ok) {
            try {
                // wait if there are no events - should not happen but best to be safe
                synchronized (RealTimeMonitor.getEvents()) {
                    waitforevents = RealTimeMonitor.getEvents().isEmpty() ? true : false;
                }
                while (waitforevents) {
                    System.out.println("waiting for more events");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        System.out.println("sleep failed?");
                        System.out.println(e);
                        this_thread_ok = false;
                    }
                    synchronized (RealTimeMonitor.getEvents()) {
                        waitforevents = RealTimeMonitor.getEvents().isEmpty() ? true : false;
                    }
                }

                // get next event at the correct time
                synchronized (RealTimeMonitor.getEvents()) {
                    nextEvent = RealTimeMonitor.getEvents().firstKey();  // the lowest timestamp, the oldest event.
                    // JM. lets try this: remove an event w/o wait if not at state we want
                    thisJob = RealTimeMonitor.getEvents().remove(nextEvent);
                    // inetresting ?
                    if (!(thisJob.getState().equals("Scheduled") || thisJob.getState().equals("Done")
                            || thisJob.getState().equals("Aborted"))) {
                        continue;  // skip this event   
                    }
                }

                long nowEpoch = Calendar.getInstance().getTimeInMillis();
                long wait = nextEvent.getTime() - nowEpoch;
                // cap the wait - needed in the case that the next event is say
                //2 minutes in the future, otherwise the incoming updates will
                //not get looked at until after their time has passed.

                wait = (wait < 5000) ? wait : 5000;   // miliseconds
                if (wait > 0) {
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        System.out.println("sleep failed?");
                        System.out.println(e);
                        this_thread_ok = false;
                    }
                }
                // handle the gLite event
                // 

                if (Job.class.isInstance(thisJob)) {
                    String id = ((Job) thisJob).getID();
                    O.b("event id: " + id);
                    String rb = ((Job) thisJob).getRB();
                    O.b("event rb: " + rb);
                    String ce = ((Job) thisJob).getCE();
                    O.b("event ce: " + ce);

                    // limit transfers according to choices
                    String rbChoices = RealTimeMonitor.getRBList();
                    String voChoices = RealTimeMonitor.getVOList();
                    boolean chosen = false;
                    if (rbChoices.equals("all")) {
                        chosen = true;
                    } else {
                        String thisRB = new String("-" + rb + "-");
                        if (rbChoices.indexOf(thisRB) > 0) {
                            chosen = true;
                        }
                    }
                    if (chosen) {
                        if (!voChoices.equals("all")) {
                            String thisVO = new String("-" + ((Job) thisJob).getVO() + "-");
                            if (!(voChoices.indexOf(thisVO) > 0)) {
                                chosen = false;
                            }
                        }
                    }
                    if (IPLookup.containsKey(rb)) {
                        String rbID = IPLookup.get(rb);
                        Site rbSite = RealTimeMonitor.getSites().get(rbID);
                        rbSite.pushRBJob((Job) thisJob);
                        if (IPLookup.containsKey(ce)) {
                            String ceID = IPLookup.get(ce);
                            Site ceSite = RealTimeMonitor.getSites().get(ceID);
                            ceSite.pushCEJob((Job) thisJob);
                            String state = thisJob.getState();
                            // only do transfers if in the last 5 minutes - and
                            //choice above are true
                            long updateEpoch = thisJob.getUpdate().getTime();
                            //System.out.println(" chosen? "+ chosen + "on time ? " + ((nowEpoch - updateEpoch) < 300000));
                            try {
                                if (RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName("Transfers Layer").isEnabled()) {
                                    if (chosen && ((nowEpoch - updateEpoch) < 300000)) {

                                        // create unique_id to avoid problem when we
                                        //might have job aborting and scheduling close to
                                        //each other but in two updates...
                                        synchronized (RealTimeMonitor.getTfsSyncCollection()) {
                                            if (state.equals("Scheduled")) {
                                                thisTransfer = new Transfer(id, "s",
                                                        rbSite.getLatitude(),
                                                        rbSite.getLongitude(),
                                                        ceSite.getLatitude(),
                                                        ceSite.getLongitude());
                                                String unique_id = new String(id + "-" + state);
                                                if (!RealTimeMonitor.getTransfers().containsKey(unique_id)) {
                                                    RealTimeMonitor.getTransfers().put(unique_id, thisTransfer);
                                                }
                                            } else {
                                                if (state.equals("Done")) {
                                                    thisTransfer = new Transfer(id, "d",
                                                            ceSite.getLatitude(),
                                                            ceSite.getLongitude(),
                                                            rbSite.getLatitude(),
                                                            rbSite.getLongitude());
                                                    String unique_id = new String(id + "-" + state);
                                                    if (!RealTimeMonitor.getTransfers().containsKey(unique_id)) {
                                                        RealTimeMonitor.getTransfers().put(unique_id, thisTransfer);
                                                    }
                                                } else {
                                                    if (state.equals("Aborted")) {
                                                        thisTransfer = new Transfer(id, "a",
                                                                ceSite.getLatitude(),
                                                                ceSite.getLongitude(),
                                                                rbSite.getLatitude(),
                                                                rbSite.getLongitude());
                                                        String unique_id = new String(id + "-" + state);                                            //synchronized (Transfers) {
                                                        if (!RealTimeMonitor.getTransfers().containsKey(unique_id)) {
                                                            RealTimeMonitor.getTransfers().put(unique_id, thisTransfer);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, " check if layer (gLite) Transfers Layer is enabled failed ?", ex);
                            }

                        }
                    }
                } else if (PandaJob.class.isInstance(thisJob)) {
                    String id = UUID.randomUUID().toString();
                    String rbSiteID = ((PandaJob) thisJob).getRBsite();
                    String ceSiteID = ((PandaJob) thisJob).getCEsite();
                    Site rbSite = RealTimeMonitor.getSites().get(rbSiteID);
                    Site ceSite = RealTimeMonitor.getSites().get(ceSiteID);
                    //String id = ((PandaJob)thisJob).getID();
                    String state = ((PandaJob) thisJob).getState();
                    // panda Transfers enabled ?
                    try {
                        if (RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName("Panda Transfers Layer").isEnabled()) {
                            if (rbSite != null && ceSite != null) {
                                makeTransfer(id, rbSite, ceSite, state, RealTimeMonitor.getPandaTransfers(), RealTimeMonitor.getPtfsSyncCollection());
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, " check if layer Panda Transfers Layer is enabled failed ?", ex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void makeTransfer(String id, Site rbSite, Site ceSite, String state, Hashtable<String, Transfer> transfers, Collection synchColl) {
        // we are using only one Transfer type, but 2 distinct transfer collections
        {
            // todo synch collection has to be a parameter
            // use the hastable argument directly, rather than RealTimeMonitor.getTransfers
            // create unique_id to avoid problem when we
            //might have job aborting and scheduling close to
            //each other but in two updates...
            Transfer transfer;
            synchronized (synchColl) {
                if (state.equals("Scheduled")) {
                    transfer = new Transfer(id, "s",
                            rbSite.getLatitude(),
                            rbSite.getLongitude(),
                            ceSite.getLatitude(),
                            ceSite.getLongitude());
                    String unique_id = new String(id + "-" + state);
                    if (!transfers.containsKey(unique_id)) {
                        transfers.put(unique_id, transfer);
                    }
                } else {
                    if (state.equals("Done")) {
                        transfer = new Transfer(id, "d",
                                ceSite.getLatitude(),
                                ceSite.getLongitude(),
                                rbSite.getLatitude(),
                                rbSite.getLongitude());
                        String unique_id = new String(id + "-" + state);
                        if (!transfers.containsKey(unique_id)) {
                            transfers.put(unique_id, transfer);
                        }
                    } else {
                        if (state.equals("Aborted")) {
                            transfer = new Transfer(id, "a",
                                    ceSite.getLatitude(),
                                    ceSite.getLongitude(),
                                    rbSite.getLatitude(),
                                    rbSite.getLongitude());
                            String unique_id = new String(id + "-" + state);                                            //synchronized (Transfers) {
                            if (!transfers.containsKey(unique_id)) {
                                transfers.put(unique_id, transfer);
                            }
                        }
                    }
                }
            }
        }
    }
}
