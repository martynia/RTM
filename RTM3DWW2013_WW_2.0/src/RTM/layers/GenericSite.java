/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.layers;

import gov.nasa.worldwind.Locatable;
import gov.nasa.worldwind.render.Renderable;
import javax.swing.JLabel;

/**
 *
 * @author martynia
 */
public interface GenericSite extends Renderable, Locatable{
    public String getID();
    public String getName();
    public String getLongName();
    public double getLatitude();
    public double getLongitude();
    public String getGridGuide();
    public JLabel getLogoLabel();
    public String getCountry();
    public void addLogo(String logoAddress);
    public void addMainInfo(String id, String name, String longname, String latitude, String longitude, String country);
}
