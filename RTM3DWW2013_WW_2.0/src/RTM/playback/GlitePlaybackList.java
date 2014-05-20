/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.playback;

import RTM.config.Configurator;
import java.util.List;

/**
 *
 * @author Janusz Martyniak
 */
public class GlitePlaybackList<String> extends RingList<String> implements Playback<String> {

    private GlitePlaybackList(List<String> l) {
        super(l);
        System.out.println("#######----> gLite playback list ctor!");
    }

    public GlitePlaybackList<String> createPlaybackList() {
        return new GlitePlaybackList(Configurator.getInstance().getGlitePlaybackList());
    }

    static {
        PlaybackFactory.getInstance().registerPlaybackList("gLite", new GlitePlaybackList(Configurator.getInstance().getGlitePlaybackList()));
    }
    //private RingList<String> ring; // buffer to hold auctual playback filenames in a ring list

    public List<String> getRingList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
