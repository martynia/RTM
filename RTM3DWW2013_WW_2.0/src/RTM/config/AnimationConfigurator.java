/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.config;

/**
 *
 * @author martynia
 */
public interface AnimationConfigurator extends Configurable{
    public boolean isAnimationEnabled();
    public void setAnimationEnabled();
    public String getAnimatorID();
}
