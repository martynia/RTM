/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author GridPP
 */
public class QuitAction extends AbstractAction{
    private static final long serialVersionUID = 426204280515L;  
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }

}
