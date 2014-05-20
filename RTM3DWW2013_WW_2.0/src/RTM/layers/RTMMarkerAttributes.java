/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.layers;

import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;

/**
 *
 * @author martynia
 */
public class RTMMarkerAttributes extends BasicMarkerAttributes {

    private Object custom;

    public RTMMarkerAttributes() {
        super();
    }

    public RTMMarkerAttributes(RTMMarkerAttributes attr) {
        super(attr);
    }

    public RTMMarkerAttributes(Material material, String shapeType, double opacity) {
        super(material, shapeType, opacity);
    }

    public RTMMarkerAttributes(Material material, String shapeType, double opacity, double markerPixels, double minMarkerSize) {
        super(material, shapeType, opacity,markerPixels, minMarkerSize);
    }
    
    public RTMMarkerAttributes(Material material, String shapeType, double opacity, double markerPixels, double minMarkerSize, double maxMarkerSize) {
       super(material, shapeType, opacity,markerPixels, minMarkerSize, maxMarkerSize); 
    }
    public void addCustomAttribute(Object obj) {
        this.custom = obj;
    }

    public Object getCustomAttribute() {
        return custom;
    }
}
