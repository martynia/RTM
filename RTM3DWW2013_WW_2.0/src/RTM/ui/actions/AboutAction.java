/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.actions;

import RTM.RealTimeMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author GridPP
 */
public class AboutAction extends AbstractAction {

    private static final long serialVersionUID = 426204280515L;

    public AboutAction() {
        super();
        putValue(NAME, "About");
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
    }

    public void actionPerformed(ActionEvent e) {
        RealTimeMonitor.showAboutIFrame();
    }
}
