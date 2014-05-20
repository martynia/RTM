/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.actions;

import RTM.RealTimeMonitor;
import gov.nasa.worldwind.layers.Layer;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 *
 * @author martynia
 */
public class PhedexAction extends AbstractAction{

    public PhedexAction(String string) {
        super(string);
        for ( String lname : actionLayers) {
           Layer layer = RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName(lname);
           if(layer!=null) {
           map.put(lname, new LayerSelectionAction(layer));
           } else {
               System.out.println(" ERROR; Layer "+lname+ " has not been defined");
           }
        } 
    }

    public void actionPerformed(ActionEvent ae) {
        for ( String lname : actionLayers) {
            LayerSelectionAction l = map.get(lname);
            if(l!=null) l.actionPerformed(ae);
        }
    }
    private String [] actionLayers={"FTS Layer","FTS Transfer Layer","FTS Site Markers","FTS Annotations","FTS Disks"};
    HashMap<String, LayerSelectionAction> map = new HashMap<String, LayerSelectionAction>();
}
