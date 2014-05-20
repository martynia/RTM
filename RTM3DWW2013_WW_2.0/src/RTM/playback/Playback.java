/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.playback;

import java.util.List;

/**
 *
 * @author martynia
 */
public interface Playback<E>{

    public Playback<E> createPlaybackList();
    public List<E> getRingList();
    
}
