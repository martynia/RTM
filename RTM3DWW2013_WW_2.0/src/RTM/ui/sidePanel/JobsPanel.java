/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.ui.sidePanel;

import RTM.RealTimeMonitor;
import RTM.datasource.glite.Job;
import RTM.layers.Site;
import RTM.ui.jobInFrame.JobsTablePanel;
import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Gidon Moont, Mikhail Khrypach, Janusz Martyniak
 */
public class JobsPanel extends JPanel{
    private static final long serialVersionUID = 15051973L;
    /**
     * Create a Jobs panel to host a sortable table.
     * @see JobsTablePanel
     * @param site site to use.
     * @param type coulde be <code>'ce'</code> (otherwise <code>'rb'</code>)
     * @param ip   hostname of teh CE (or RB) depending on <code>type</code>
     */
    public JobsPanel (Site site, String type, String ip){
        super(new BorderLayout());
        JPanel north = new JPanel(new BorderLayout());
        this.add(north, BorderLayout.NORTH);
        JPanel controls = new JPanel(new BorderLayout());
        controls.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add(controls, BorderLayout.SOUTH);

        Hashtable<String, Job> AllGridJobs = null;
        if (type.equals("ce")) {
            AllGridJobs = site.getGridCEJobs();
        } else {
            AllGridJobs = site.getGridRBJobs();
        }

        String voChoices = RealTimeMonitor.getRBList();
        String rbChoices = RealTimeMonitor.getVOList();

        if (ip.equals("all") && RealTimeMonitor.getVOList().equals("all") && RealTimeMonitor.getRBList().equals("all")) {
            JobsTablePanel jtp = new JobsTablePanel(AllGridJobs);
            jtp.setFocusable(false);
            jtp.populateTable();
            north.add(jtp);
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
            JobsTablePanel jtp = new JobsTablePanel(SelectedGridJobs);
            jtp.setFocusable(false);
            jtp.populateTable();
            north.add(jtp);
        }
    }
}
