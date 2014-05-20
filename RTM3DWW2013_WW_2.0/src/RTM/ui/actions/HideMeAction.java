/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Janusz Martyniak
 */
public class HideMeAction extends AbstractAction{
    private static final long serialVersionUID = 426204280515L;
    private Component parent;
    public HideMeAction(Component parent) {
        super();
        this.parent = parent;
        putValue(NAME,"Hide Me!");
        putValue(MNEMONIC_KEY,new Integer(KeyEvent.VK_M));
    }

    public void actionPerformed(ActionEvent e) {
        
        parent.setVisible(false);
    }
}
