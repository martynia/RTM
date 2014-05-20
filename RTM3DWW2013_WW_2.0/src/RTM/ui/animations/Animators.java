/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.animations;

import RTM.config.Configurator;
import java.util.HashMap;

/**
 *  Animators factory (singleton)
 * @author martynia
 */
public class Animators {

    private HashMap<String, Animator> registeredAnimators = new HashMap<String, Animator>();
    private static Animators instance;

    public static Animators getInstance() {
        if (instance == null) {
            synchronized (Animators.class) {
                instance = new Animators();
            }
        }
        return instance;
    }

    public void registerAnimator(String AnimatorID, Animator a) {
        registeredAnimators.put(AnimatorID, a);
    }

    public Animator createAnimator() {
        String animatorID = Configurator.getInstance().getAnimatorID();
        return ((Animator) registeredAnimators.get(animatorID)).createAnimator();
    }
}
