/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.layers;

import RTM.RealTimeMonitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Contains a HashMap of List of layers indexed by feed item name
 * feeds -> Jobs (name)
 *       -> Geant(name)
 *       -> Phedex(name)
 * 
 * Jobs-> glite (elem)
 *     -> panda (elem)
 *     -> sites (elem)
 * 
 * @author Janusz Martyniak
 */
public class Feed {

    public Feed(String name) {
        this.name = name;
        // items.put(name, new ArrayList<String>());
    }

    /*
    public Feed(String name, List<String> layers) {
    this.name = name;
    items.put(name,layers);
    }
     */
    /*
     * elem -  a feeds submenu/feed menu
     * layer - a layer which contributes to submenu. 
     */
    public void addLayerToFeed(String elem, String layer) {
        List<String> layers;
        if (items.containsKey(elem)) {
            layers = items.get(elem);
            layers.add(layer);
        } else {
            layers = new ArrayList<String>();
            layers.add(layer);
            items.put(elem, layers);
        }
    }

    HashMap<String, List<String>> getFeedItems() {
        return items;
    }

    List<String> getLayerList(String item) {
        return items.get(item);
    }

    /**
     * Enable all layers for the feed.
     * @param enable 
     */
    public void enableAll(boolean enable) {
        for (String key : items.keySet()) {
            enableFeedItem(key, enable);
        }
    }

    public void enableFeedItem(String item, boolean enable) {
        List<String> l = items.get(item);
        for (String layer : l) {
            try {
                RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName(layer).setEnabled(enable);
            } catch (NullPointerException npe) {
                System.out.println(" cannot enable/disable  layer " + layer + " it does not exist");
            }
        }
    }
    public String toString() {
        StringBuffer sb=null, list=null;
        for(String item: items.keySet()) {
           list=null;
           for(List<String> l : items.values()) {
               list.append(l.toString());
           } 
           sb.append(item).append(":").append(list);
        }
        return sb.toString();
    }
    private String name;
    private HashMap<String, List<String>> items = new HashMap<String, List<String>>();
}
