/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.animations;

import RTM.RealTimeMonitor;
import RTM.config.ini4j.Ini4jConfigWrapper;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

/**
 *
 * @author Janusz Martyniak
 */
public class FollowPath implements ItemListener {

    private final Timer timer;
    private FollowPathTask task = null;
    private final ArrayList<PathElement> path = new ArrayList<PathElement>();

    public FollowPath() {
        timer = new Timer();
        try {
            Ini4jConfigWrapper t = new Ini4jConfigWrapper();
            t.loadConfiguration("rtmConfig");
            Ini.Section pathSec = t.getSection("path");
            String[] names = pathSec.getAll("place", String[].class);
            double[] lat = pathSec.getAll("latitude", double[].class);
            double[] lon = pathSec.getAll("longitude", double[].class);
            double altitude = pathSec.get("altitude", double.class);
            double[] zoom = pathSec.getAll("zoom", double[].class);
            long[] sleep = pathSec.getAll("sleep", long[].class); // sleep when reached the destination
            long[] time = pathSec.getAll("time", long[].class); // time to animate this path element

            if (!(names.length == lat.length && lat.length == lon.length && lon.length == zoom.length && zoom.length == time.length
                    && time.length == sleep.length)) {
                throw new InvalidFileFormatException("Mumber names/lat/lon/zoom/sleep/time elements is different !:\n"
                        + "names " + names.length + "\n"
                        + "latitude " + lat.length + "\n"
                        + "longitude " + lon.length + "\n"
                        + "zoom " + zoom.length + "\n"
                        + "sleep" + sleep.length + "\n"
                        + "time " + time.length);
            }
            double z, alt;
            long tt, s;
            for (int i = 0; i < names.length; i++) {
                z = Math.max(1., zoom[i]);
                tt = Math.abs(time[i]);
                s = Math.abs(sleep[i]);
                alt = Math.max(1000., altitude);
                path.add(new PathElement(names[i], lat[i], lon[i], alt / z, tt, z, s));
            }

        } catch (IOException ex) {
            Logger.getLogger(FollowPath.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException nfe) {
            Logger.getLogger(FollowPath.class.getName()).log(Level.SEVERE, null, nfe);
        }
    }

    public void itemStateChanged(ItemEvent ie) {
        if (ie.getStateChange() == ItemEvent.SELECTED) {
            task = new FollowPathTask(path);
            timer.schedule(task, new Date());  //now    
        } else {
            task.stopAnimations();
        }
    }

    private class PathElement {

        private final Position pos;
        private final long time, sleep;
        private final double zoom;
        private final String name;

        public PathElement(String name, double lat, double lon, double elevation, long time, double zoom, long sleep) {
            pos = Position.fromDegrees(lat, lon, elevation);
            this.time = time;
            this.sleep = sleep;
            this.zoom = zoom;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Position getPos() {
            return pos;
        }

        public long getSleep() {
            return sleep;
        }

        public long getTime() {
            return time;
        }

        public double getZoom() {
            return zoom;
        }
    }

    private class FollowPathTask extends TimerTask {

        private Boolean animating;
        private Boolean stop = false;
        final BasicOrbitView view;
        ArrayList<PathElement> p;
        Iterator<PathElement> it;

        public FollowPathTask(ArrayList<PathElement> paths) {
            super();
            view = (BasicOrbitView) RealTimeMonitor.getAf().getWwd().getView();
            view.getViewInputHandler().stopAnimators();
            p = paths;
            it = p.iterator();
        }

        @Override
        public void run() {
            stop=false;
            System.out.println(" EDT ? " + SwingUtilities.isEventDispatchThread());
            // make an initial jump
            if (it.hasNext()) {
                Position curPosition = view.getEyePosition();
                PathElement elem = it.next();
                Position startPosition = elem.getPos();
                long sleep = elem.getSleep();
                // go there quicly (3.5 seconds):
                long initial = 3500;
                view.addEyePositionAnimator(initial, curPosition, startPosition);
                try {
                    Thread.sleep(initial + 1000 * sleep);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FollowPathTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            while (true) {

                if (p.isEmpty()) {
                    return;
                }

                if (stop) {
                    break;
                }
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {

                            animating = view.getViewInputHandler().isAnimating();
                        }
                    });

                    if (!animating) {
                        System.out.println(" Memory " + Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB ");

                        Position curPosition = view.getEyePosition();
                        Position endPosition;
                        PathElement elem;
                        double zoom, elevation;
                        long time, sleep;
                        if (it.hasNext()) {
                            elem = it.next();
                            endPosition = elem.getPos();
                        } else {
                            it = p.iterator();
                            elem = it.next(); // we KNOW the list is not empty 
                        }
                        endPosition = elem.getPos();
                        sleep = elem.getSleep();
                        time = elem.getTime();

                        System.out.println(" Visiting " + elem.getName());
                        System.out.println("Lat= " + endPosition.getLatitude().getDegrees());
                        System.out.println("Lon= " + endPosition.getLongitude().getDegrees());
                        System.out.println("Elev=" + view.getEyePosition().getElevation());
                        System.out.println("zoom=" + view.getZoom());
                        view.addEyePositionAnimator(1000 * time, // time to iterate, begin position, end position
                                curPosition, endPosition); // (end position has a modifield elevation already)
                        
                        synchronized (this) {
                            // the wait below is meant to be interrupted when a ItemEvent.SELECTED is  false.
                            wait(1000 * (time + sleep)); // sleep is an extra nap at the end of an animation)
                            
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(FollowPathTask.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(FollowPathTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            view.getViewInputHandler().stopAnimators(); // unsafe here ?
        }

        private synchronized void stopAnimations() {
            stop=true;
            view.getViewInputHandler().stopAnimators();
            notify(); // cut the wait, so another animation can kick in, or a normal operation is resumed.
        }
    }
}
