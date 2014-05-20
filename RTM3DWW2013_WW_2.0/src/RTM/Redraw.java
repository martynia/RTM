/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM;

import RTM.layers.Transfer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Redraw is used to create thread that would redraw given a Gl canvas every n
 * milliseconds. It also removes transfers that had been completed.
 * @author Mikhail Khrypach
 * JM: can handle multiple collections
 */
class Redraw implements Runnable {

    private final ArrayList<Collection> list;
    int size = 0;

    public Redraw(ArrayList<Collection> list) {
        this.list = list;
    }

    public void run() {
        while (true) {
            for (Collection c : list) {
                synchronized (c) {
                    for (Iterator<Transfer> i = (Iterator<Transfer>) c.iterator(); i.hasNext();) {
                        if (i.next().isFinished()) {
                            i.remove();
                        }
                    }
                }

            }
            // redraw after having transferred all collections and removed finished transfers
            RealTimeMonitor.getAf().getWwd().redraw();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Redraw.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
