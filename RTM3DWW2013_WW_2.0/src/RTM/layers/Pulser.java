// Pulser
package RTM.layers;

import RTM.O;
import java.util.LinkedList;

public class Pulser implements Runnable {

    private final int max = 10; // hardcoded here and in Globe.CE - fix later (was 20, JM)
    LinkedList<CircleSize> css = new LinkedList<CircleSize>();
    private boolean this_thread_ok = true;
    private Boolean freeze = false;

    public void addCS(CircleSize cs) {
        css.add(cs);
    }

    public void setThreadStop() {
        this_thread_ok = false;
    }

    public void freeze(Boolean freeze) {
        this.freeze = freeze;
    }

    /**
     * Vary circle size. The value is stored in CircleSize.
     * The actual drawing is performed in Site.render().
     */
    public void run() {

        while (this_thread_ok) {

            for (CircleSize cs : css) {

                int size = cs.get();

                if (size >= (max - 1)) {
                    cs.setD(-1);
                } else {
                    if (size <= 0) {
                        cs.setD(1);
                    }
                }

                size = size + cs.getD();
                //O.p(Integer.toString(size));

                cs.set(size);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("sleep failed?");
            }
        }
    }
}
