/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.playback;

import RTM.config.Configurator;
import java.util.List;

/**
 * A Panda playback handling class. 
 * @author Janusz Martyniak
 */
public class PandaPlayback<String> implements Playback<String> {

    private PandaPlayback(List<String> l) {
        ring = new RingList<String>(l);
        System.out.println("#######----> Panda playback ctor!");
    }

    public PandaPlayback<String> createPlaybackList() {
        return new PandaPlayback(Configurator.getInstance().getPandaPlaybackList());
    }
    public List<String> getRingList() {
        return ring;
    }
    static {
        PlaybackFactory.getInstance().registerPlaybackList("Panda", new PandaPlayback(Configurator.getInstance().getPandaPlaybackList()));
    }
    
    private RingList<String> ring; // buffer to hold auctual playback filenames in a ring list
}
