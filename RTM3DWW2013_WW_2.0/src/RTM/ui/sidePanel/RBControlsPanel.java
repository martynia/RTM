/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.sidePanel;

import RTM.RealTimeMonitor;
import RTM.layers.Site;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Mikhail Khrypach
 */
public class RBControlsPanel extends JPanel {
    private static final long serialVersionUID = 426204280515L;
    private String choices = null;
    private final Color colour = Color.orange;
    private String allPossibleChoices = new String("");
    private final int numberOfRB = 200;
    private JCheckBox[] checkBoxes = new JCheckBox[numberOfRB];

    /**
     *
     */
    public RBControlsPanel() {
        super(new BorderLayout());

        choices = RealTimeMonitor.getRBList();
        this.setName("Resource Broker Controls");
        
        JPanel list = new JPanel(new GridLayout(100, 2));
        JScrollPane scroll = new JScrollPane(list);
        scroll.setVerticalScrollBarPolicy(scroll.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(scroll.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(RealTimeMonitor.getSidePanel().getSize().width, RealTimeMonitor.getSidePanel().getSize().height/10*9));

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

        this.add(scroll);
        scroll.setVisible(true);

        JPanel controls = new JPanel(new GridLayout(0, 4));
        JButton checkAll = new JButton("Select All");
        checkAll.setFocusable(false);
        checkAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                choices = "all";
                for (int line = 0; line < numberOfRB; line++) {
                    try {
                        checkBoxes[line].setSelected(true);
                    } catch (Exception ex) {}
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
                    } catch (Exception ex) {}
                }
            }
        });
        controls.add(unCheckAll);

        JButton apply = new JButton("Apply");
        apply.setFocusable(false);
        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RealTimeMonitor.setRBList(choices);
                RealTimeMonitor.reChooseSites();
            }
        });
        controls.add(apply);

        checkAll.setBackground(colour);
        unCheckAll.setBackground(colour);
        apply.setBackground(colour);
        controls.setBackground(colour);
        controls.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.add(controls, BorderLayout.SOUTH);
        this.setVisible(true);
    }
}
