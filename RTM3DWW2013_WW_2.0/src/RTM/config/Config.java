/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.config;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * RTM configuration class. Define frame size/ decoration etc, read command line switches.
 * @author Janusz Martyniak
 */
public class Config {
    private static String RTM_clientVersion="Version 2.10 Beta (16 July 2013)";

    private Config() {
    }
    
    public static String getVersion() {
        return RTM_clientVersion;
    }
    public static String getWS_URL() {
        return "http://"+getWS_host();
    }
    public static String getWS_host() {
        return "rtmws.hep.ph.ic.ac.uk";
    }
    /**
     * Obtain the RTM frame size.
     * @return the frame size.
     */
    public static Dimension getFrameSize() {
        return new Dimension((int) (sd.width * scale), (int) (sd.height * scale));
    }

    /**
     * 
     * @return frame width
     */
    public static int getFrameWidth() {
        return (int) (sd.width * scale);
    }

    /**
     *  
     * @return frame height
     */
    public static int getFrameHeight() {
        return (int) (sd.height * scale);
    }

    /**
     * 
     * @return frame x offset
     */
    public static int getXoffset() {
        return (int) ((1. - scale) / 2 * sd.width);
    }

    /**
     * 
     * @return frame x offset
     */
    public static int getYoffset() {
        return (int) ((1. - scale) / 2 * sd.height);
    }

    public static boolean isApplet() {
        return isApplet;
    }
    
    public static void setApplet(boolean applet) {
        isApplet = applet;
    }

    @Override
    public Config clone() throws CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
    }
    private static Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
    private static double scale = 0.85;
    private static boolean isApplet = false;
}