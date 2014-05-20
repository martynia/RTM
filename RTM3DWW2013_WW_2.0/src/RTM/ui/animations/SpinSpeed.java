
package RTM.ui.animations;

/** 
 * 
 * A message object to carry the speed value
 * @author Janusz Martyniak
 */
public class SpinSpeed {
     /**
     * Rotation speed, measured in turns/24 hours.
     * @return  speed
     */
    public double getSpeed() {
        return speed;
    }
    /**
     * Set the number of turns in 24 hours.
     * @param time to make a full turn [min] 
     */
    public void setSpeed(double timeToTurn) {
        this.speed = 24.*60/timeToTurn;
    }

    public SpinSpeed() {
    // retain the default speed (1/h)
    }
    /**
     * Store time to make a full turn [min]
     * @param turns 
     */
    public SpinSpeed(double timeToTurn){
        // lowets time is 5 minutes, max time is set to 24 hours.
        timeToTurn = Math.max(5, Math.min(timeToTurn, 24.*60));
        speed=24.*60/timeToTurn;
    }
    
    private double speed=24.;   // 24 times faster, i.e. full turn in 60 minutes
}
