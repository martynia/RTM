/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.layers;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Janusz Martyniak
 */
public abstract class AbstractSite implements GenericSite{
//
    protected Vec4 p = null;
    protected double x = 0;
    protected double y = 0;
    protected double z = 0;
    protected double theta = 0;
    protected double phi = 0;
    protected double ce_scale_factor = 1;
    protected double[][] coordinates = null;
    protected double zoom = 0;
// define protected site data here
    protected String id = "";
    protected String name = "";
    protected String longname = "";
    protected double latitude = 0;
    protected double longitude = 0;
    protected String location = ""; //i.e London
    protected String country = "";
    protected String website;
    private JLabel logoLabel;
// leave render() unimplemented
    public String getID() {
        return id;
    }

    public String getName() {
        return name;        
    }

    public String getLongName() {
        return longname;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getGridGuide() {
        return website;
    }

    public JLabel getLogoLabel() {
        return logoLabel;
    }

    public void addMainInfo(String id, String name, String longname, String latitude, String longitude, String country) {
        this.id = id;
        this.name = name;
        this.longname = longname;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.country = country;  
    }
    public Position getPosition() {
        return new Position(Angle.fromDegrees(latitude), Angle.fromDegrees(longitude), 0.0);
    }

    public String getCountry() {
        return country;
    }

    public void addLogo(final String logoAddress) {
        try {

            // EDT
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    try {
                        URL logoURL = new URL(logoAddress);
                        logoLabel = new JLabel();
                        Image logoImage = Toolkit.getDefaultToolkit().createImage(logoURL);
                        logoLabel.setIcon(new ImageIcon(logoImage));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(NetworkSite.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Cannot find logo?");
        }
    }
    @Override
    public String toString() {
     return "SiteId:"+getID()+" name:"+getName()+" Lat:"+getLatitude()+" Long:"+getLongitude()+"\n";   
    }
        /**
     * Test if a layer the site belongs to is enabled.
     * @return true if enabled, false otherwise.
     */
    public Boolean isLayerEnabled(String layer) {
        return RTM.RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName(layer).isEnabled();
    }
}
