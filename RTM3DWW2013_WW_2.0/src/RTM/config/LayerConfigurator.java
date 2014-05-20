/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.config;

import java.util.HashMap;

/**
 * Interface to RTM and WW layer configuration 
 * @author Janusz Martyniak
 */
public interface LayerConfigurator {
    public boolean isEnabled(String layer);
    public void setEnabled(String layer);
    public HashMap<String, Boolean> getValidLayers();
}
