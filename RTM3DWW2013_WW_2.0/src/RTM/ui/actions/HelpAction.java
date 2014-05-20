/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.actions;

import RTM.RealTimeMonitor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Janusz Martyniak
 */
public class HelpAction extends AbstractAction {

    private static final long serialVersionUID = 426204280515L;

    public void actionPerformed(ActionEvent e) {
        RealTimeMonitor.showHelpIFrame();
    }
}
