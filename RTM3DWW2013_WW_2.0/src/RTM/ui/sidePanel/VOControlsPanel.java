/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.ui.sidePanel;

import RTM.RealTimeMonitor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Mikhail Khrypach
 */
public class VOControlsPanel extends JPanel{

    private final Color colour = Color.cyan;
    private static final long serialVersionUID = 15051973L;
    private String choices = null;
    private String allPossibleChoices = new String("");
    private JCheckBox[] checkBoxes = new JCheckBox[100];

    public VOControlsPanel(){
        super(new BorderLayout());

        choices = RealTimeMonitor.getVOList();
        this.setName("Virtual Organisation Controls");

        JPanel list = new JPanel(new GridLayout(25, 2));
        JScrollPane scroll = new JScrollPane(list);
        scroll.setVerticalScrollBarPolicy(scroll.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(scroll.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(RealTimeMonitor.getSidePanel().getSize().width, RealTimeMonitor.getSidePanel().getSize().height/10*9));

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

        this.add(scroll);
        scroll.setVisible(true);

        JPanel controls = new JPanel(new GridLayout(0, 4));
        JButton checkAll = new JButton("Select All");
        checkAll.setFocusable(false);
        checkAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                choices = "all";
                for (int line = 0; line < 100; line++) {
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
                for (int line = 0; line < 100; line++) {
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
                RealTimeMonitor.setVOList(choices);
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
