/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.animations;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author martynia
 */
class SpinSpeedAction extends AbstractAction {
    
    private final double speed;
    private final SpinSpeed message;
    /**
     * Custom constructor
     * @param label Action label
     * @param speed associated speed
     */
    SpinSpeedAction(String label, double speed, SpinSpeed message) {
        super(label);
        this.speed=speed;
        this.message=message;
    }
    /**
     * Pass the selected speed to the Spin class
     * @param ae 
     */
    public void actionPerformed(ActionEvent ae) {
        synchronized(message) {
           System.out.println("########### SPEED change requested ##########");
           message.setSpeed(speed); 
           message.notify();
        }
    }
    
}
