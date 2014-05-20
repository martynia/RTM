/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.animations;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author martynia
 */
public class SpinSpeedSelector extends AbstractAction{

    public SpinSpeedSelector(String string, SpinSpeed message) {
        super(string);
        this.message=message;
    }

    public void actionPerformed(ActionEvent ae) {
        //System.out.println(" menu clicked !");
    }

    public void addMenuItems(JMenuItem spinSpeed) {
        ButtonGroup group = new ButtonGroup();
        for(int i = 0; i < speeds.length ; i++) {
            SpinSpeedAction action = new SpinSpeedAction((int)speeds[i]+"  minutes.",speeds[i], message);
            JRadioButtonMenuItem rb = new JRadioButtonMenuItem(action);
            group.add(rb);
            spinSpeed.add(rb);
            if(i==0) rb.setSelected(true);
        }
        
    }
    private final SpinSpeed message;
    private double [] speeds={60., 30., 15., 10., 5., 2. };
}
