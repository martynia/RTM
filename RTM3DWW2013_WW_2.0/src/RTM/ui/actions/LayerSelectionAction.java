/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.actions;

import RTM.RealTimeMonitor;
import RTM.ui.sidePanel.AboutPanel;
import RTM.ui.sidePanel.SitePanel;
import gov.nasa.worldwind.layers.Layer;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Action assigned to RTM layers ( like Sites Layer or Transfers layer).
 * This action is called from within GliteAction and PandaAction for example
 * This causes relevant layers to be enabled by menu clicks.
 * 
 * @author Janusz Martyniak
 */
public class LayerSelectionAction extends AbstractAction {

    private static final long serialVersionUID = 426204280515L;

    public LayerSelectionAction(Layer layer) {
        super(layer.getName());
        this.layer = layer;
    }

    public void actionPerformed(ActionEvent e) {
        // enable a layer for this action
        layer.setEnabled(!layer.isEnabled());
        
        if (layer.getName().equals("Transfers Layer") || layer.getName().equals("Panda Transfers Layer")) {
            JTabbedPane tabs = RealTimeMonitor.getAf().getSidePanel().getTabPnl();
            int ntabs = tabs.getTabCount();
            System.out.println(" number of tabbed components " + ntabs);
            for (int i = 0; i < ntabs; i++) {
                System.out.println(" component: " + tabs.getTabComponentAt(i).getClass().getName());
                //if (tabs.getTabComponentAt(i) instanceof SitePanel) {
                // AboutPanel cannot be cast, so:
                if (tabs.getComponentAt(i) instanceof AboutPanel) {
                    continue;
                }
                JScrollPane pane = (JScrollPane) tabs.getComponentAt(i);
                SitePanel siteP = (SitePanel) pane.getViewport().getComponent(0);
                JTabbedPane graphs = siteP.getGraphsPane();
                int ngraphs = graphs.getTabCount();
                System.out.println(" number of tabbed graphs " + ngraphs);
                for (int g = 0; g < ngraphs; g++) {
                    String title = graphs.getTitleAt(g);
                    JLabel graph = (JLabel) graphs.getComponentAt(g);
                    //graph.setVisible(layer.isEnabled());
                    System.out.println(" graph title " + title + " at " + g);
                }
                siteP.customizeContent(e);
                //}
            }
            RealTimeMonitor.reChooseSites();
        }
    }
    private Layer layer;
}
