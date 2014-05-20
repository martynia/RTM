/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM;

import java.awt.BorderLayout;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author martynia
 */
public class StartupProgressBar extends JPanel {

    private static final long serialVersionUID = 15051973L;
    private JPanel panel;
    private JProgressBar pbar;
    private JLabel bLabel;
    static final int MY_MINIMUM = 0;
    static final int MY_MAXIMUM = 100;

    public StartupProgressBar() throws Exception {
        super();
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new Exception(" Not EDT Exception (StartupProgressBar");
        }
        bLabel = new JLabel("Starting ... ");
        setLayout(new BorderLayout());
        this.setBackground(Color.YELLOW);
        pbar = new JProgressBar();
        pbar.setSize(250, 50);
        pbar.setMaximum(MY_MINIMUM);
        pbar.setMaximum(MY_MAXIMUM);
        pbar.setForeground(Color.PINK);
        add(pbar, BorderLayout.CENTER);
        add(bLabel, BorderLayout.SOUTH);
    }

    public void updateBar(final int newVal, final String newLabel) {
        if (SwingUtilities.isEventDispatchThread()) {
            pbar.setValue(newVal);
            bLabel.setText(newLabel);
        } else {
            if(!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    public void run() {
                        pbar.setValue(newVal);
                        bLabel.setText(newLabel);
                    }
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(StartupProgressBar.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(StartupProgressBar.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            } else {
                pbar.setValue(newVal);
                bLabel.setText(newLabel); 
            }
        }
    }
}
