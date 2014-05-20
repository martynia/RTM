/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

/**
 * Action assigned to the Jobs menu item (not feeds->jobs)
 * @author martynia
 */
public class JobsAction extends AbstractAction{

    public JobsAction(String string) {
        super(string);
    }

    public void actionPerformed(ActionEvent ae) {
        boolean sel=false;
        if(ae.getSource() instanceof JCheckBoxMenuItem) {
            
           sel =  
           ((JCheckBoxMenuItem)ae.getSource()).isSelected(); 
           System.out.println(" selected ? "+sel);
        }
        int m = RTM.RTMMenuBar.getInstance().getMenuCount();
        System.out.println(" Number of menus in the menubar " + m);
        for(int i =0; i < m; i++) {
            JMenu menu = RTM.RTMMenuBar.getInstance().getMenu(i);
            if(menu==null) {
                System.out.println(" No menu at position " + i);
                continue;
            }
            System.out.println(" position  "+ i + " name: " + menu.getName() + " text (was label): "+menu.getText());
            if(menu.getText().equals("Jobs")) { // this is menu corresponding to our menu item !
                menu.setEnabled(sel);
                int subm = menu.getItemCount();
                System.out.println(" Jobs #submenus "+ subm);
                for(int k = 0; k < subm; k++) {
                    System.out.println(" scanning Jobs submenus " + menu.getItem(k).getClass().getName());
                    if(menu.getItem(k) instanceof JCheckBoxMenuItem ) {
                        // all items under Job clicked.
                        menu.getItem(k).doClick();
                        //menu.getItem(k).setSelected(sel);
                        System.out.println(" selecting " + sel + " item "+ menu.getItem(k).getText());
                    } else {
                        System.out.println(" only passing through (no instanceof) item "+ menu.getItem(k).getText());
                    }
                }
            }
        }
    }
    
}
