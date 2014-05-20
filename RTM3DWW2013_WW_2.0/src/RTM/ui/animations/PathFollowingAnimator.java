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
public class PathFollowingAnimator extends Animator{

    public void animate() {
        AbstractButton button=RTMMenuBar.getInstance().getFollowPathButton();
        button.doClick();
    }

    @Override
    public Animator createAnimator() {
        return new PathFollowingAnimator();
    }
    static
	{
		Animators.getInstance().registerAnimator("path", new PathFollowingAnimator());
	}
}
