/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.animations;

import RTM.RealTimeMonitor;
import RTM.config.ini4j.Ini4jConfigWrapper;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/**
 *
 * @author Janusz Martyniak
 */
public class Spin implements ItemListener {

    private SpinTask task = null;
    private final Timer timer;
    private long delay = 200;
    private Boolean stop = false;
    private final SpinSpeed message;
    private final JMenu linkedMenu;

    public Spin(SpinSpeed message, JMenu linkedMenu) {
        timer = new Timer();
        this.message=message;
        this.linkedMenu=linkedMenu;
    }

    @Override
    public void itemStateChanged(ItemEvent ie) {
        if(ie.getStateChange()==ItemEvent.SELECTED) {
            System.out.println(" SElected ");
            stop = false;
            task = new SpinTask();    
            timer.schedule(task, new Date());  //now

            linkedMenu.setEnabled(true);
        } else {
            System.out.println(" deSElected ");
            task.cancel();
            synchronized(message) {
                // kill the wait and the stop flag will do the rest
                message.notify();
            }
            stop = true;
            task = null;
            linkedMenu.setEnabled(false);
        } 
        if (task == null) {
            
        } else {           
            
        }
    }

    class SpinTask extends TimerTask {

        final BasicOrbitView view;
        private Boolean animating;
        private double speed = 24.0; // full turn in 1 hour
        private double latitude = 50.0; // fly over London, roughly

        public SpinTask() {
            view = (BasicOrbitView) RealTimeMonitor.getAf().getWwd().getView();
            view.getViewInputHandler().stopAnimators();
            try {

                Ini4jConfigWrapper t = new Ini4jConfigWrapper();
                
                t.loadConfiguration("rtmConfig");
                speed = Double.parseDouble(t.getSectionValue("spin", "speed"));
                
                latitude = Double.parseDouble(t.getSectionValue("spin", "latitude"));
                // keep the current longitude, adjust latitude as requested, keep the elevation !
                Position curPosition = view.getEyePosition();
                System.out.println("Position "+ curPosition);
                Position startPosition = Position.fromDegrees(latitude, curPosition.getLongitude().getDegrees(), view.getEyePosition().getElevation());
                // go there quicly (3.5 seconds):
                view.addEyePositionAnimator(3500, curPosition, startPosition);
            } catch (IOException ex) {
                Logger.getLogger(Spin.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void run() {

            while (true) {

                if (stop) {
                    break;
                }
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {

                        public void run() {

                            animating = view.getViewInputHandler().isAnimating();
                        }
                    });

                    if (!animating) {
                        Position curPosition = view.getEyePosition();
                        Angle angle = curPosition.getLongitude().add(Angle.fromDegrees(-15.0));
                        //System.out.println("(will add new animator) longitude " + angle.getDegrees());
                        Position endPosition = Position.fromDegrees(curPosition.getLatitude().getDegrees(), angle.getDegrees());
                        // time to iterate 15 degrees of the arc:
                        long time = (long) (3600000 / (speed));  // 1_hour/speed_factor (default factor is 24.)
                        view.addEyePositionAnimator(time, // time to iterate [ms], begin position, end position
                                curPosition, new Position(endPosition, view.getEyePosition().getElevation())); // position, elevation
                        synchronized(message) {
                            message.wait(time);
                            // we have to stop the animator at this point. At premature interrupt (speed change requested by
                            // the user, we update the speed, and restart the animator with the correct one.
                            view.getViewInputHandler().stopAnimators();
                            speed = message.getSpeed();
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Spin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(Spin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            view.getViewInputHandler().stopAnimators(); // unsafe here ?
        }
    }
}
