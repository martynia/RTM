// XMLUpdatesHandler
package RTM.datasource.glite;

import RTM.RealTimeMonitor;
import RTM.layers.Site;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;


import java.util.Calendar;
import java.sql.Timestamp;

/**
 * 
 * @author martynia
 */
public class XMLUpdatesHandler extends DefaultHandler {

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
    private int event_count = 0;
    private int early_events = 0;
    private int late_events = 0;
    private final long delay = 180000;
    private long nowEpoch = Calendar.getInstance().getTimeInMillis();
    //playback only
    private Timestamp creationTimestamp = null;
    private Timestamp updateTimestamp=null;
    private String timestamp = ""; // this is the update timestamp string. not used by real data
    // end playback

    /**
     * XMLUpdates handler is created periodically, at a given second to a minute and
     * reads a complete update.
     * @param last_updatenumber the last update number the handler was created for.
     */
    public XMLUpdatesHandler(String last_updatenumber) {
        this.last_updatenumber = last_updatenumber;
    }

    /**
     *
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     */
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

    /**
     *
     * @param uri
     * @param localName
     * @param qName
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("job")) {

            if (!updatenumber.equals(last_updatenumber)) {

                event_count++;
                if (creationTimestamp != null) {
                    // for playback ONLY claculate fake update time from creation time (which is 3 minutes in the past)
                    // and the updtae timestamp which is common for all jobs in the update.
                    // the per job time difference is updateTimestamp - update
                    // we have to add this period to our fake 'now' (==creationTimestamp)
                    update = new Timestamp(creationTimestamp.getTime()+(updateTimestamp.getTime()-Timestamp.valueOf(update).getTime())).toString();
                    //System.err.println("Per Job update timestamp"+update);
                }
                Job thisJob = new Job(id, rb, registered, state, update, rtm_timestamp, ce, queue, ui, vo);

                Timestamp eventTiming = null;
                long actualEvent = Timestamp.valueOf(update).getTime() + Calendar.getInstance().get(Calendar.MILLISECOND); // hackish but we need unique IDs in the TreeMap
                //System.out.println(" delay = " + (nowEpoch - actualEvent) );
                // first, events in the past, no more than delay ago...
                if (((nowEpoch - actualEvent) >= 0 && (nowEpoch - actualEvent) < delay)) //JM add > 0 bit
                {
                    eventTiming = new Timestamp(actualEvent + delay);
                } else {
                    // events in the future
                    if (nowEpoch < actualEvent) // (JM) never happens ! Included above !!!! meant for event in future
                    {
                        // probably badly configured RB - warn but handle when it says so!
                        eventTiming = new Timestamp(actualEvent);
                        early_events++;
                    } else {
                        // have an event more than 3 minutes in the past
                        // move by X minutes to a time after now for display
                        late_events++;  // events in the past more than delay (3 mins)
                        long keep_shifting = actualEvent + delay;
                        while (keep_shifting < nowEpoch) {
                            keep_shifting = keep_shifting + 60000;
                        }
                        eventTiming = new Timestamp(keep_shifting); // shifted above nowEpoch.
                    }
                }

                synchronized (RealTimeMonitor.getEvents()) {
                    while (RealTimeMonitor.getEvents().containsKey(eventTiming)) {
                        // System.out.println( "shifting eventTiming" ) ;
                        eventTiming = new Timestamp(eventTiming.getTime() + 1);
                    }
                    if (RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName("Transfers Layer").isEnabled()) {
                        RealTimeMonitor.getEvents().put(eventTiming, thisJob);
                    }
                }
                
            } // end update count test

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
                                                    } else {
                                                        if (qName.equals("timestamp")) {
                                                        timestamp = current_value;
                                                        updateTimestamp = Timestamp.valueOf(timestamp+":00");
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
        current_value = "";
    }

    /**
     *
     * @param ch
     * @param start
     * @param len
     */
    @Override
    public void characters(final char[] ch, final int start, final int len) {
        current_value = current_value.concat(new String(ch, start, len));
    }

    /**
     * Get number of events in this update.
     * @return number of events in this update
     */
    public int getEventsCount() {
        return event_count;
    }

    /**
     *
     * @return
     */
    public int getEarlyEventsCount() {
        return early_events;
    }

    /**
     *
     * @return
     */
    public int getLateEventsCount() {
        return late_events;
    }

    /**
     * Return current update number.
     * @return current update number.
     */
    public String getUpdateNumber() {
        return updatenumber;
    }

    /**
     * For playback only. Record the handler creation time, one per update. 
     * @param timestamp 
     */
    void setPlaybackTimestamp(Timestamp timestamp) {
        this.creationTimestamp = timestamp;
    }
}
