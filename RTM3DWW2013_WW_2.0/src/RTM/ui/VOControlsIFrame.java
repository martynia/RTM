// VOControlsIFrame
// limit of 100 Virtual Organisations....
package RTM.ui;

import RTM.RealTimeMonitor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

import java.awt.event.*;
import javax.swing.event.*;

public class VOControlsIFrame extends JInternalFrame {

    private final Color colour = Color.cyan;
    private static final long serialVersionUID = 15051973L;
    private String choices = null;
    private String allPossibleChoices = new String("");
    private JCheckBox[] checkBoxes = new JCheckBox[100];

    public VOControlsIFrame() {
        choices = RealTimeMonitor.getVOList();

        this.setClosable(true);
        this.setIconifiable(true);
        this.setMaximizable(false);
        this.setResizable(false);

        this.setTitle("Virtual Organisation Controls");

        JPanel panel = new JPanel(new BorderLayout());

        JPanel list = new JPanel(new GridLayout(0, 6));

        int voCount = 0;
        synchronized (RealTimeMonitor.getActiveVOs()) {
            Iterator iter = RealTimeMonitor.getActiveVOs().iterator();
            while (iter.hasNext()) {

                String vo = (String) iter.next();

                if (!vo.equals("unknown")) {

                    allPossibleChoices = allPossibleChoices.concat("--" + vo + "--");

                    if (choices.equals("all")) {
                        checkBoxes[voCount] = new JCheckBox(vo, true);
                    } else {
                        String checkVO = new String("--" + vo + "--");
                        if (choices.indexOf(checkVO) >= 0) {
                            checkBoxes[voCount] = new JCheckBox(vo, true);
                        } else {
                            checkBoxes[voCount] = new JCheckBox(vo, false);
                        }
                    }
                    checkBoxes[voCount].setFocusable(false);
                    checkBoxes[voCount].addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            JCheckBox cb = (JCheckBox) e.getSource();
                            String checkVO = new String("--" + cb.getText() + "--");
                            if (cb.isSelected()) {
                                choices = choices.concat(checkVO);
                            } else {
                                if (choices.equals("all")) {
                                    choices = allPossibleChoices;
                                }
                                choices = choices.replace(checkVO, "");
                            }
                        }
                    });

                    checkBoxes[voCount].setBackground(colour);
                    list.add(checkBoxes[voCount]);

                    voCount++;

                }

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
                for (int line = 0; line < 100; line++) {
                    try {
                        checkBoxes[line].setSelected(true);
                    } catch (Exception ex) {
                    }

                }
            }
        });
        controls.add(checkAll);

        JButton unCheckAll = new JButton("Select None");
        unCheckAll.setFocusable(false);
        unCheckAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                choices = "";
                for (int line = 0; line < 100; line++) {
                    try {
                        checkBoxes[line].setSelected(false);
                    } catch (Exception ex) {
                    }

                }
            }
        });
        controls.add(unCheckAll);

        JButton apply = new JButton("Apply");
        apply.setFocusable(false);
        apply.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                RealTimeMonitor.setVOList(choices);
                // need to trigger the Sites to correct themselves to the new choices
                RealTimeMonitor.reChooseSites();
            }
        });
        controls.add(apply);
        checkAll.setBackground(colour);
        unCheckAll.setBackground(colour);
        apply.setBackground(colour);
        controls.setBackground(colour);

        controls.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(controls, BorderLayout.SOUTH);

        this.add(panel);

        this.addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                //rtm.setVOList(choices);//overKill.put("rbList", choices);
                // need to trigger the Sites to correct themselves to the new choices

                //rtm.reChooseSites();
                RealTimeMonitor.nullVOControlsIFrame();
                // JM - the keyboard listener is added to the OpenGL canvas, return focus there on close.
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
