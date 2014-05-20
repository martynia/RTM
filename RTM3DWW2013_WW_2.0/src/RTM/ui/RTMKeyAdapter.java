package RTM.ui;

import RTM.RealTimeMonitor;
import RTM.RTMMenuBar;
import java.awt.AWTException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;

/**
 * General key adapter for all objects in the rtm application
 * @author Mikhail Khrypach
 */
public class RTMKeyAdapter extends KeyAdapter {

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'c' || e.getKeyChar() == 'C') {
            //jm for (JInternalFrame inf : ((JDesktopPane) RealTimeMonitor.getAf().getContentPane()).getAllFrames()) {
            for (JInternalFrame inf : RealTimeMonitor.getAf().getAllFrames()) {
                inf.doDefaultCloseAction();
            }
            if (!RealTimeMonitor.useInFrames()) {
                RealTimeMonitor.getSidePanel().getCloseButton().doClick();
            }
        }
        if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
            RealTimeMonitor.showRBControlsIFrame();
        }
        if (e.getKeyChar() == 'v' || e.getKeyChar() == 'V') {
            RealTimeMonitor.showVOControlsIFrame();
        }
        if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A') {
            RealTimeMonitor.showAboutIFrame();
        }
        if (e.getKeyChar() == 'h' || e.getKeyChar() == 'H') {
            RealTimeMonitor.showHelpIFrame();
        }
        if (e.getKeyChar() == 'n' || e.getKeyChar() == 'N') {
            RealTimeMonitor.toggleJobStats();
        }
        if (e.getKeyChar() == 'm' || e.getKeyChar() == 'M') {
            RTMMenuBar.getInstance().setVisible(!RTMMenuBar.getInstance().isVisible());
        }
        if (e.getKeyChar() == 'z' || e.getKeyChar() == 'Z') {
            try {
                robot = new java.awt.Robot();
                robot.mouseWheel(-2);
            } catch (AWTException ex) {
                Logger.getLogger(RTMKeyAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == 'x' || e.getKeyChar() == 'X') {
            try {
                robot = new java.awt.Robot();
                robot.mouseWheel(+2);
            } catch (AWTException ex) {
                Logger.getLogger(RTMKeyAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private java.awt.Robot robot;
}
