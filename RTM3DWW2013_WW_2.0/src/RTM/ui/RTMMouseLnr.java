package RTM.ui;

import RTM.RealTimeMonitor;
import RTM.layers.Pickable;
import RTM.layers.Site;
import gov.nasa.worldwind.Locatable;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *This MouseListener is added to the RealTimeMonitor.getAf.getWwd().
 * It is used to get Site objects when mouse is clicked on the globe.
 *
 * @author Mikhail Khrypach
 * JM change Site to Locatable
 */
public class RTMMouseLnr implements MouseListener {

    public void mouseClicked(MouseEvent e) {
        //disable autocentering when mouse is clicked
        ((WorldWindowGLCanvas) e.getComponent()).getView().stopMovement();
        //getting position on the globe under coursor
        Position p = ((WorldWindowGLCanvas) e.getComponent()).getCurrentPosition();
        if (p != null) {
            System.out.println("Position selected: " + p.toString());
            //Getting a collection of sites nearest to the coursor
            //closeness is spesified in SitePositionMap
            Iterable<Locatable> itbl = RealTimeMonitor.getSPM().getSites(p);
            if (itbl != null) {
                System.out.println("Number of sites retrieved: " + ((LinkedList<Locatable>) itbl).size());
                for (Iterator<Locatable> i = itbl.iterator(); i.hasNext();) {
                    Pickable s = (Pickable) i.next();
                    System.out.println(s.toString());
                    if(RealTimeMonitor.useInFrames()){
                        s.showInFrame();
                    } else {
                        s.showPanel();
                    }
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
