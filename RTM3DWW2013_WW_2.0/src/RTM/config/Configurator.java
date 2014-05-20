/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.config;

import RTM.config.ini4j.Ini4jConfigWrapper;
import gov.nasa.worldwind.layers.Layer;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ini4j.Ini;

/**
 *
 * @author martynia
 */
public class Configurator implements AnimationConfigurator, LayerConfigurator, PlaybackConfigurator {

    private boolean enabled = false;
    private boolean configOK = false;
    private String animID;
    private Ini4jConfigWrapper t;
    private static Configurator instance;
    private HashMap<String, Boolean> validLayers;

    public static Configurator getInstance() {
        if (instance == null) {
            synchronized (Configurator.class) {
                instance = new Configurator();
            }
        }
        return instance;
    }
    private List<String> gplist = new ArrayList<String>();
    private List<String> pplist = new ArrayList<String>();

    ;

    private Configurator() {
        try {
            t = new Ini4jConfigWrapper();
            t.loadConfiguration("rtmConfig");
            configOK = true; // configuration loaded
            animID = t.getSectionValue("animation", "enabled"); // i.e spin
            if (animID != null) {
                enabled = true;
            }

            getLayers();
            // pass the playback list  dependent filename and a pointer to the relevant list
            defPlayback(t.getSectionValue("playback", "gLite"), gplist);
            defPlayback(t.getSectionValue("playback", "Panda"), pplist);

        } catch (IOException ex) {
            // load failed!
            Logger.getLogger(Configurator.class.getName()).log(Level.SEVERE, null, ex);
            enabled = false;
        }
    }

    public boolean isAnimationEnabled() {
        return enabled;
    }

    public void setAnimationEnabled() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getAnimatorID() {
        return animID;
    }

    public boolean wantFullScreen() {

        if (configOK) {
            String fs = t.getSectionValue("properties", "fullscreen");
            if (fs == null || !fs.equalsIgnoreCase("yes")) {
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

    public boolean wantFullScreenAtStart() {

        //(this escures that config is OK )
        String fs = t.getSectionValue("properties", "startfullscreen");
        if (fs == null || !fs.equalsIgnoreCase("yes")) {
            return false;
        }
        return true;
    }

    public boolean isEnabled(String layer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEnabled(String layer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get a validated list of layers specified in the config file. Layers which
     * do not exist (i.e. with misspelled names) are not included.
     * @return list of layers
     */
    private void getLayers() {
        validLayers = new HashMap<String, Boolean>();
        //  TODO npe when no 'layers' section?
        Ini.Section layers = t.getSection("layers");
        Set<String> keys = layers.keySet();
        for (String key : keys) {
            System.out.println(" Key: " + key + " value " + layers.fetch(key));
            Layer l = RTM.RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName(key);

            if (l != null) {

                if (layers.fetch(key).equalsIgnoreCase("yes")) {
                    validLayers.put(key, Boolean.valueOf(true));
                } else {
                    validLayers.put(key, Boolean.valueOf(false));
                }

            } else {
                Logger.getLogger(Configurator.class.getName()).log(Level.WARNING, null, "Your are trying to eneble a non existing layer (typo?). Check your config file");
            }
        }
        return;
    }

    /**
     * Get all layers listed in the configuration file.
     * @return list of layers
     */
    public HashMap<String, Boolean> getValidLayers() {
        return validLayers;
    }

    public List<String> getGlitePlaybackList() {
        return gplist;
    }

    /**
     * Read list of files for the filename and add them to a list
     * passed in
     * @param listFilename a filename with a list of playback files, one per row.
     * @param list a list returned.
     */
    private void defPlayback(String listFilename, List<String> list) {
        InputStream fis;
        BufferedReader br;
        String line;

        //list = new ArrayList<String>();
        //String listFilename = t.getSectionValue("playback", "gLite");
        if (listFilename != null) {
            try {
                fis = new FileInputStream(listFilename);
                br = new BufferedReader(new InputStreamReader(fis));
                String prefix=null;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("prefix=")) {
                        prefix=line.substring(7);
                    } else if (!line.startsWith("#")) {
                        list.add("file://"+prefix+line);
                    }
                }

                // Done with the file
                br.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Configurator.class.getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                Logger.getLogger(Configurator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<String> getPandaPlaybackList() {
        return pplist;
    }

    /**
     * Get the name of a file containing the playback list (list of actual playback files). 
     * @return name of a file
     */
    public String getPandaPlaybackListFilename() {
        return t.getSectionValue("playback", "Panda");
    }
}
