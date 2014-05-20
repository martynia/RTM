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
public class NetworkAction extends AbstractAction{

    public NetworkAction(String string) {
        super(string);
        for ( String lname : actionLayers) {
           map.put(lname, new LayerSelectionAction(RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName(lname))); 
        } 
    }

    public void actionPerformed(ActionEvent ae) {
        for ( String lname : actionLayers) {
            LayerSelectionAction l = map.get(lname);
            if(l!=null) l.actionPerformed(ae);
        }
    }
    private String [] actionLayers={"Network Sites Layer"};
    HashMap<String, LayerSelectionAction> map = new HashMap<String, LayerSelectionAction>();
}
