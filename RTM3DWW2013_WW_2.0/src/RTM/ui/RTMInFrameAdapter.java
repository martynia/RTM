/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui;

import RTM.RealTimeMonitor;
import RTM.layers.Site;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 *
 * @author Mikhail Khrypach
 */
public class RTMInFrameAdapter extends InternalFrameAdapter {

    private final Site site;

    public RTMInFrameAdapter(Site s) {
        this.site = s;
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        // JM - return focus to the OpenGL canvas on close
        RealTimeMonitor.getAf().getWwd().requestFocusInWindow();
        site.nullFrame();
    }
}
