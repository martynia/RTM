// AboutIFrame
package RTM.ui.jobInFrame;

import RTM.RealTimeMonitor;
import RTM.datasource.glite.Job;
import RTM.layers.Site;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class JobsIFrame extends JInternalFrame {

    private static final long serialVersionUID = 15051973L;
    private JPanel north = null;

    public JobsIFrame() {
        this.setClosable(true);
        this.setIconifiable(true);
        this.setMaximizable(true);
        this.setResizable(true);

        JPanel panel = new JPanel(new BorderLayout());
        north = new JPanel(new BorderLayout());
        panel.add(north, BorderLayout.NORTH);

        JPanel controls = new JPanel(new BorderLayout());

        controls.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(controls, BorderLayout.SOUTH);

        this.add(panel);
        this.setOpaque(true);

        Dimension sd = RealTimeMonitor.getAf().getSize();
        this.setLocation(sd.width / 4, sd.height / 4);

        this.addInternalFrameListener(new InternalFrameAdapter() {

            public void internalFrameClosed(InternalFrameEvent e) {
                RealTimeMonitor.nullJobsFrame();
            }
        });
        this.addKeyListener(RealTimeMonitor.getKeyAdapter());

        //jm RealTimeMonitor.getAf().getContentPane().add(this, JLayeredPane.PALETTE_LAYER);
        RealTimeMonitor.getAf().add(this, JLayeredPane.PALETTE_LAYER);
    }

    public void setJobs(Hashtable<String, Job> GridJobs) {
        JobsTablePanel jtp = new JobsTablePanel(GridJobs);
        jtp.setFocusable(false);
        jtp.populateTable();
        north.removeAll();
        north.add(jtp);
    }

    public void setJobs(Site site, String type, String ip) {

        if (type.equals("ce")) {
            if (ip.equals("all")) {
                this.setTitle(site.getName() + " / all CEs");
            } else {
                this.setTitle(site.getName() + " / CE " + ip);
            }
        } else {
            if (ip.equals("all")) {
                this.setTitle(site.getName() + " / all RBs");
            } else {
                this.setTitle(site.getName() + " / RB " + ip);
            }
        }

        Hashtable<String, Job> AllGridJobs = null;
        if (type.equals("ce")) {
            AllGridJobs = site.getGridCEJobs();
        } else {
            AllGridJobs = site.getGridRBJobs();
        }

        String voChoices = RealTimeMonitor.getRBList();
        String rbChoices = RealTimeMonitor.getVOList();

        if (ip.equals("all") && voChoices.equals("all") && rbChoices.equals("all")) {
            this.setJobs(AllGridJobs);
        } else {
            Hashtable<String, Job> SelectedGridJobs = new Hashtable<String, Job>();
            if (type.equals("ce")) {
                for (Enumeration A = AllGridJobs.elements(); A.hasMoreElements();) {
                    Job job = (Job) A.nextElement();
                    String checkVO = new String("--" + job.getVO() + "--");
                    String checkRB = new String("--" + job.getRB() + "--");
                    if ((ip.equals("all") || ip.equals(job.getCE())) && (voChoices.equals("all") || (voChoices.indexOf(checkVO) >= 0)) && (rbChoices.equals("all") || (rbChoices.indexOf(checkRB) >= 0))) {
                        SelectedGridJobs.put(job.getID(), job);
                    }
                }
            } else {
                for (Enumeration A = AllGridJobs.elements(); A.hasMoreElements();) {
                    Job job = (Job) A.nextElement();
                    String checkVO = new String("--" + job.getVO() + "--");
                    String checkRB = new String("--" + job.getRB() + "--");
                    if ((ip.equals("all") || ip.equals(job.getRB())) && (voChoices.equals("all") || (voChoices.indexOf(checkVO) >= 0)) && (rbChoices.equals("all") || (rbChoices.indexOf(checkRB) >= 0))) {
                        SelectedGridJobs.put(job.getID(), job);
                    }
                }
            }
            this.setJobs(SelectedGridJobs);
        }
        this.pack();
        this.setVisible(true);
    }
}
