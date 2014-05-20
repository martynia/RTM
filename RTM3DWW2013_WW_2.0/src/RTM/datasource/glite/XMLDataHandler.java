// XMLDataHandler
package RTM.datasource.glite;

import RTM.RealTimeMonitor;
import RTM.layers.Site;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.Calendar;
import java.sql.Timestamp;

public class XMLDataHandler extends DefaultHandler {

    private boolean within_id = false;
    private boolean within_rb = false;
    private boolean within_registered = false;
    private boolean within_state = false;
    private boolean within_update = false;
    private boolean within_rtmts = false;
    private boolean within_ce = false;
    private boolean within_queue = false;
    private boolean within_ui = false;
    private boolean within_vo = false;
    private boolean within_updatenumber = false;
    private String current_value = "";
    private String id = "";
    private String rb = "";
    private String registered = "";
    private String state = "";
    private String update = "";
    private String rtm_timestamp = "";
    private String ce = "";
    private String queue = "";
    private String ui = "";
    private String vo = "";
    private String updatenumber = "";
    private String last_updatenumber = "";
    private int job_count = 0;
    private int event_count = 0;
    private int early_events = 0;
    private int late_events = 0;
    private final long delay = 180000;
    private Hashtable<String, String> IPLookup = new Hashtable<String, String>();
    private long nowEpoch = Calendar.getInstance().getTimeInMillis();

    public XMLDataHandler() {
        // populate IPLookup Hashtable
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

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("job")) {
            // clear values
            id = "";
            rb = "";
            registered = "";
            state = "";
            update = "";
            rtm_timestamp = "";
            ce = "";
            queue = "";
            ui = "";
            vo = "";
        } else {
            if (qName.equals("id")) {
                within_id = true;
            } else {
                if (qName.equals("rb")) {
                    within_rb = true;
                } else {
                    if (qName.equals("registered")) {
                        within_registered = true;
                    } else {
                        if (qName.equals("status")) {
                            within_state = true;
                        } else {
                            if (qName.equals("update")) {
                                within_update = true;
                            } else {
                                if (qName.equals("rtmts")) {
                                    within_rtmts = true;
                                } else {
                                    if (qName.equals("ce")) {
                                        within_ce = true;
                                    } else {
                                        if (qName.equals("queue")) {
                                            within_queue = true;
                                        } else {
                                            if (qName.equals("ui")) {
                                                within_ui = true;
                                            } else {
                                                if (qName.equals("vo")) {
                                                    within_vo = true;
                                                } else {
                                                    if (qName.equals("updatecount")) {
                                                        within_updatenumber = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("job")) {

            job_count++;

            Job thisJob = new Job(id, rb, registered, state, update, rtm_timestamp, ce, queue, ui, vo);

            if ((nowEpoch - Timestamp.valueOf(update).getTime()) < delay) {

                // put job into event queue
                event_count++;

                Timestamp eventTiming = null;
                long actualEvent = Timestamp.valueOf(update).getTime() + Calendar.getInstance().get(Calendar.MILLISECOND); // hackish but we need unique IDs in the TreeMap

                if ((nowEpoch - actualEvent) < delay) {
                    eventTiming = new Timestamp(actualEvent + delay);
                } else {
                    if (nowEpoch < actualEvent) {
                        // probably badly configured RB - warn but handle when it says so!
                        eventTiming = new Timestamp(actualEvent);
                        early_events++;
                    } else {
                        // have an event more than two minutes in the past
                        // move by X minutes to a time after now for display
                        late_events++;
                        long keep_shifting = actualEvent + delay;
                        while (keep_shifting < (nowEpoch - delay)) {
                            keep_shifting = keep_shifting + 60000;
                        }
                        eventTiming = new Timestamp(keep_shifting);
                    }
                }

                synchronized (RealTimeMonitor.getEvents()) {
                    while (RealTimeMonitor.getEvents().containsKey(eventTiming)) {
                        eventTiming = new Timestamp(eventTiming.getTime() + 1);
                    }
                    if (RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName("Transfers Layer").isEnabled()) {
                        RealTimeMonitor.getEvents().put(eventTiming, thisJob);
                    }
                }

            } else {

                // put job onto site
                try {
                    Site rbSite = RealTimeMonitor.getSites().get(IPLookup.get(rb));
                    rbSite.pushRBJob(thisJob);
                } catch (Exception e) {
                } //  System.out.println( "non existent RB? " + rb ) ; }
                if (!ce.equals("unknown")) {
                    try {
                        Site ceSite = RealTimeMonitor.getSites().get(IPLookup.get(ce));
                        ceSite.pushCEJob(thisJob);
                    } catch (Exception e) {
                    }
                }

            } // end of job tag test

        } else {
            if (qName.equals("id")) {
                within_id = false;
                id = current_value;
            } else {
                if (qName.equals("rb")) {
                    within_rb = false;
                    rb = current_value;
                } else {
                    if (qName.equals("registered")) {
                        within_registered = false;
                        registered = current_value;
                    } else {
                        if (qName.equals("status")) {
                            within_state = false;
                            state = current_value;
                        } else {
                            if (qName.equals("update")) {
                                within_update = false;
                                update = current_value;
                            } else {
                                if (qName.equals("rtmts")) {
                                    within_rtmts = false;
                                    rtm_timestamp = current_value;
                                } else {
                                    if (qName.equals("ce")) {
                                        within_ce = false;
                                        ce = current_value;
                                    } else {
                                        if (qName.equals("queue")) {
                                            within_queue = false;
                                            queue = current_value;
                                        } else {
                                            if (qName.equals("ui")) {
                                                within_ui = false;
                                                ui = current_value;
                                            } else {
                                                if (qName.equals("vo")) {
                                                    within_vo = false;
                                                    vo = current_value;
                                                    synchronized (RealTimeMonitor.getActiveVOs()) {
                                                        RealTimeMonitor.getActiveVOs().add(vo);
                                                    }
                                                } else {
                                                    if (qName.equals("updatecount")) {
                                                        within_updatenumber = false;
                                                        updatenumber = current_value;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        current_value = "";
    }

    @Override
    public void characters(final char[] ch, final int start, final int len) {
        current_value = current_value.concat(new String(ch, start, len));
    }

    public int getJobsCount() {
        return job_count;
    }

    public int getEventsCount() {
        return event_count;
    }

    public int getEarlyEventsCount() {
        return early_events;
    }

    public int getLateEventsCount() {
        return late_events;
    }

    public String getUpdateNumber() {
        return updatenumber;
    }
}
