// RBControlsIFrame
// limit of 200 RBs... (fix temp)
package RTM.ui;

import RTM.RealTimeMonitor;
import RTM.layers.Site;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.event.*;

public class RBControlsIFrame extends JInternalFrame {

    private final Color colour = Color.orange;
    private static final long serialVersionUID = 15051973L;
    //private Hashtable<String, Site> Sites = null;
    //private Hashtable<String, Object> overKill = null;
    private String choices = null;
    private String allPossibleChoices = new String("");
    private JCheckBox[] checkBoxes = null;
    private final int numberOfRB = 200;

    public RBControlsIFrame() {

        choices = RealTimeMonitor.getRBList(); //(String) overKill.get("rbList");

        checkBoxes = new JCheckBox[numberOfRB];

        this.setClosable(true);
        this.setIconifiable(true);
        this.setMaximizable(false);
        this.setResizable(false);

        this.setTitle("Resource Broker Controls");

        JPanel panel = new JPanel(new BorderLayout());

        for (int line = 0; line < numberOfRB; line++) {
            checkBoxes[line] = null;
        }

        JPanel list = new JPanel(new GridLayout(0, 4));

        int rbCount = 0;

        TreeSet<String> ActiveRBs = new TreeSet<String>();

        synchronized (RealTimeMonitor.getSites()) {
            for (Enumeration S = RealTimeMonitor.getSites().elements(); S.hasMoreElements();) {
                Site thisSite = (Site) S.nextElement();
                ArrayList<String> RBs = thisSite.getRBs();
                Iterator iterateRBs = RBs.iterator();
                while (iterateRBs.hasNext()) {
                    String rb = (String) iterateRBs.next();
                    ActiveRBs.add(rb);
                }
            }

            Iterator iter = ActiveRBs.iterator();
            while (iter.hasNext()) {

                String rb = (String) iter.next();

                allPossibleChoices = allPossibleChoices.concat("--" + rb + "--");

                if (choices.equals("all")) {
                    checkBoxes[rbCount] = new JCheckBox(rb, true);
                } else {
                    String checkRB = new String("--" + rb + "--");
                    if (choices.indexOf(checkRB) >= 0) {
                        checkBoxes[rbCount] = new JCheckBox(rb, true);
                    } else {
                        checkBoxes[rbCount] = new JCheckBox(rb, false);
                    }
                }
                checkBoxes[rbCount].setFocusable(false);
                checkBoxes[rbCount].addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        JCheckBox cb = (JCheckBox) e.getSource();
                        String checkRB = new String("--" + cb.getText() + "--");
                        if (cb.isSelected()) {
                            choices = choices.concat(checkRB);
                        } else {
                            if (choices.equals("all")) {
                                choices = allPossibleChoices;
                            }
                            choices = choices.replace(checkRB, "");
                        }
                    }
                });

                checkBoxes[rbCount].setBackground(colour);
                list.add(checkBoxes[rbCount]);

                rbCount++;

            }

        }

        list.setBackground(colour);
        list.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(list, BorderLayout.NORTH);

        JPanel controls = new JPanel(new GridLayout(0, 4));

        // buttons to (un)check all
        JButton checkAll = new JButton("Select All");
        checkAll.setFocusable(false);
        checkAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                choices = "all";
                for (int line = 0; line < numberOfRB; line++) {
                    try {
                        checkBoxes[line].setSelected(true);
                    } catch (Exception ex) {
                    } // null pointer - not important here

                }
            }
        });
        controls.add(checkAll);

        JButton unCheckAll = new JButton("Select None");
        unCheckAll.setFocusable(false);
        unCheckAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                choices = "";
                for (int line = 0; line < numberOfRB; line++) {
                    try {
                        checkBoxes[line].setSelected(false);
                    } catch (Exception ex) {
                    } // null pointer - not important here

                }
            }
        });
        controls.add(unCheckAll);

        JButton apply = new JButton("Apply");
        apply.setFocusable(false);
        apply.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                RealTimeMonitor.setRBList(choices);//overKill.put("rbList", choices);
                // need to trigger the Sites to correct themselves to the new choices

                RealTimeMonitor.reChooseSites();
            }
        });
        controls.add(apply);

        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // close the frame, the frame listener will do the job
                ((RBControlsIFrame) ((JButton) e.getSource()).getRootPane().getParent()).doDefaultCloseAction();
            }
        });
        controls.add(ok);

        checkAll.setBackground(colour);
        unCheckAll.setBackground(colour);
        apply.setBackground(colour);
        //ok.setBackground(colour);
        controls.setBackground(colour);

        controls.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(controls, BorderLayout.SOUTH);

        this.add(panel);

        this.addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                RealTimeMonitor.setRBList(choices);//overKill.put("rbList", choices);
                // need to trigger the Sites to correct themselves to the new choices

                RealTimeMonitor.reChooseSites();
                RealTimeMonitor.nullRBControlsIFrame();
                // JM - the keyboard listener is added to the OpenGL canvas, return it there on close.
                RealTimeMonitor.getAf().getWwd().requestFocusInWindow();
            }
        });

        //jm RealTimeMonitor.getAf().getContentPane().add(this, JLayeredPane.PALETTE_LAYER);
        RealTimeMonitor.getAf().add(this, JLayeredPane.PALETTE_LAYER);
        this.setOpaque(true);
        this.pack();
        Dimension sd = RealTimeMonitor.getAf().getSize();
        this.setLocation((sd.width - this.getWidth()) / 2, (sd.height - this.getHeight()) / 2);
        this.setVisible(true);
    }
}
