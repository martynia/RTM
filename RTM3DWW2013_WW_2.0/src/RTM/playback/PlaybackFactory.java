/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.playback;

import java.util.HashMap;

/**
 * Playback List factory. Creates Playback objects
 * @author J. Martyniak
 */
public class PlaybackFactory {

    private HashMap<String, Playback> registeredPlayback = new HashMap<String, Playback>();
    private static PlaybackFactory instance;

    public static PlaybackFactory getInstance() {
        if (instance == null) {
            synchronized (PlaybackFactory.class) {
                instance = new PlaybackFactory();
            }
        }
        return instance;
    }

    public void registerPlaybackList(String playbackListID, Playback a) {
        registeredPlayback.put(playbackListID, a);
    }

    public Playback createPlaybackList(String playbackListID) {
        System.out.println("Factory createPlaybackList called for "+ playbackListID);
        if (registeredPlayback.containsKey(playbackListID)) {
            System.out.println(" this id " + playbackListID + " is registered, OK");
            return ((Playback) registeredPlayback.get(playbackListID)).createPlaybackList();
        } else {
            return null;
        }
    }
}
