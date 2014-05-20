/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.actions;

import RTM.RealTimeMonitor;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 *
 * @author martynia
 */
public class PandaAction extends AbstractAction{

    public PandaAction(String string) {
        super(string);
        for ( String lname : actionLayers) {
           map.put(lname, new LayerSelectionAction(RealTimeMonitor.getAf().getWwd().getModel().getLayers().getLayerByName(lname))); 
        }  
    }

 /**
     * Cascade actions. Call relevant LayerSelectionAction(s)
     * @param ae 
     */
    public void actionPerformed(ActionEvent ae) {
        for ( String lname : actionLayers) {
            map.get(lname).actionPerformed(ae);
        }
    }
    
    private String [] actionLayers={"Panda Transfers Layer"};
    HashMap<String, LayerSelectionAction> map = new HashMap<String, LayerSelectionAction>();
}
