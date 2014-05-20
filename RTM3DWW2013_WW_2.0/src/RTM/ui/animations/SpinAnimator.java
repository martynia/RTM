/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.animations;

import RTM.RTMMenuBar;
import javax.swing.AbstractButton;

/**
 *
 * @author martynia
 */
public class SpinAnimator extends Animator{

    public void animate() {
        AbstractButton button = RTMMenuBar.getInstance().getSpinButton();
        button.doClick();
    }

    @Override
    public SpinAnimator createAnimator() {
        return new SpinAnimator();
    }
    static
	{
		Animators.getInstance().registerAnimator("spin", new SpinAnimator());
	}
}
