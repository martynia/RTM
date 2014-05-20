/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.panda;

import RTM.RealTimeMonitor;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import RTM.job.GenericJob;
import RTM.datasource.panda.xmlmapadapter.Jobsummary;
import RTM.dboard.util.MapHist;
import RTM.dboard.xml.jaxb.JobSummariesUnmarshaller;
import RTM.playback.RingList;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panda jobs events handler. Gets Panda job information from the RealTimeMonitor Apache server
 * and fill in the common RealTimeMonitor Events buffer
 * @author martynia
 */
public class PandaUpdatesHandler {

    Timer timer;
    TimerTask ert = null, est = null;
    private boolean stopped;
    private final List<String> ring;
    private final Iterator<String> ringIterator;
    private final List<String> defList = Arrays.asList("http://rtmsrv00.hep.ph.ic.ac.uk/dynamic_information/panda_all_prog.xml");
    /**
     * Create the Panda data reading system with 2 timers. Start the timers. For playback only
     * @param seconds    main event timer cycle in seconds. The slice timer 
     *                   has 10 cycles in the main cycle (10 slices=histogram bins)
     * @param delay      times(s) start with delay
     * @param ringBuffer for playback
     */
    public PandaUpdatesHandler(int seconds, int delay, List<String> ringBuffer) {
        this.ring = ringBuffer; // check if not Null ?
        
        if (ringBuffer == null) {
            ringBuffer = new RingList(defList); // this ring has length == 1
        }
        this.ringIterator = ringBuffer.iterator();
        timer = new Timer();
        ert = new EventReadingTask();
        timer.scheduleAtFixedRate(ert, delay, //initial delay
                seconds * 1000); //subsequent rate
        est = new EventSliceTask();
        timer.scheduleAtFixedRate(est, delay + 20000, //initial delay
                seconds * 100); //subsequent rate (1/10th)
    }
    /**
     * Create the Panda data reading system with 2 timers. Start the timers. For real data
     * @param seconds
     * @param delay 
     */
    public PandaUpdatesHandler(int seconds, int delay) {
        this(seconds, delay, null);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            // this will not clear event buffers, only future tasks (event reading and slices)
            // will not be executed
            boolean cancel_ert = ert.cancel();
            boolean cancel_est = est.cancel();
            stopped = true;
            Logger.getLogger(PandaUpdatesHandler.class.getName()).warning("Stopping Panda timer thread..");
        }
    }

    public boolean isStopped() {
        return stopped;
    }

    class EventReadingTask extends TimerTask {

        @Override
        public void run() {
            if (ring != null) {
                if (ringIterator.hasNext()) {
                    url = ringIterator.next();
                }
            } else {
                url= defList.get(0); // static filename for real data, as a fallback
            }
            JobSummariesUnmarshaller jsu = new JobSummariesUnmarshaller();
            Jobsummary jsum = jsu.unmarshal(url);
            // jsum is likely to be null if unmarshalling fails (exception caught in jsu.unmarshal()
            // this is critical, have to skip the whole event and all slices in it. TODO
            // store jobs stats in a syc-ed map
            if(jsum==null) {
                Logger.getLogger(EventReadingTask.class.getName()).log(Level.SEVERE, " Pand event (all slices) have not been read due to unmarshalling error");
                return; // no choice.
                
            }
            JobStatisticsHandler.getInstance().put(jsum.getSiteJobList().getMap());
            long sTime = jsum.startdatetime.toGregorianCalendar().getTimeInMillis();
            long eTime = jsum.enddatetime.toGregorianCalendar().getTimeInMillis();
            slices = new MapHist(10, sTime, eTime);
            Logger.getLogger(EventReadingTask.class.getName()).log(Level.FINE, jsu.toString());
            PandaJobFactory factory = new PandaJobFactory();
            while (true) {
                PandaJob job = factory.createJob(jsum);
                if (job == null) {
                    Logger.getLogger(EventReadingTask.class.getName()).log(Level.FINE, " ############## No more Panda jobs ############");
                    break;
                }
                slices.add(job.getTime(), job);
            }
            for (int i = 0; i < slices.getNbins(); i++) {
                System.out.println("bin " + i + " #jobs " + slices.getValue(i).size());
            }
            Logger.getLogger(EventReadingTask.class.getName()).log(Level.FINE, " execution time " + (System.currentTimeMillis() - this.scheduledExecutionTime()));
        }
    }

    class EventSliceTask extends TimerTask {

        @Override
        public void run() {
            if (currentSlice >= slices.getNbins()) {
                currentSlice = 0;// rewind
            }
            Map<java.sql.Timestamp, GenericJob> buf = slices.getValue(currentSlice);
            // have to shift the time !
            long now = System.currentTimeMillis();
            long diff;
            long sum = 0;

            Logger.getLogger(EventSliceTask.class.getName()).log(Level.FINE, " histogram: lo =" + (slices.getLo() - now) / 1000 + " hi: " + (slices.getHi() - now) / 1000);
            //timeshift =offset+(slices.getNbins()-currentSlice)*(slices.getHi()-slices.getLo())/slices.getNbins();
            long variable_offset = now - slices.getHi(); // this moves the upper limit to now.
            // now shift a given slice, so it's events are positive in time [0,60) seconds
            timeshift = variable_offset - offset + (slices.getNbins() - currentSlice) * (slices.getHi() - slices.getLo()) / slices.getNbins();
            for (Map.Entry<java.sql.Timestamp, GenericJob> entry : buf.entrySet()) {
                long shiftedTime = entry.getKey().getTime() + timeshift;
                sum = sum + shiftedTime - now;
                PandaJob shiftedJob = (PandaJob) entry.getValue();
                shiftedJob.setTime(shiftedTime);
                shiftedBuf.put(new java.sql.Timestamp(shiftedTime), shiftedJob);
            }
            // type problem, change the sclice key type to Timestamp ? Events.
            Logger.getLogger(EventSliceTask.class.getName()).log(Level.FINE, "slice " + currentSlice + " shift (s) " + timeshift / 1000 + " #jobs " + slices.getValue(currentSlice).size());
            if (RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName("Panda Transfers Layer").isEnabled()) {
                synchronized (RealTimeMonitor.getEvents()) {
                    Logger.getLogger(EventSliceTask.class.getName()).log(Level.FINE, "size before: " + RealTimeMonitor.getEvents().size());
                    RealTimeMonitor.getEvents().putAll(shiftedBuf);
                    Logger.getLogger(EventSliceTask.class.getName()).log(Level.FINE, " panda - slice in ! " + buf.size() + " jobs " + "after:" + RealTimeMonitor.getEvents().size());
                }
            }
            buf.clear();
            shiftedBuf.clear();
            currentSlice++;
        }
        private long timeshift;
        private long offset = 0; // 3*60000; //3 mins offset.
        private int currentSlice = 0;
        private Map<java.sql.Timestamp, GenericJob> shiftedBuf = new HashMap<java.sql.Timestamp, GenericJob>();
    }

    public static void main(String[] args) {
        PandaUpdatesHandler puh = new PandaUpdatesHandler(600, 0);
    }
    private String inputFile = "/tmp/panda_all_prog.xml";
    private String url;
    private MapHist slices;
}
