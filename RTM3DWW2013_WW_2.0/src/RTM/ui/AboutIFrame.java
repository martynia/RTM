// AboutIFrame
package RTM.ui;

import RTM.RealTimeMonitor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class AboutIFrame extends JInternalFrame {

    private static final long serialVersionUID = 150519732009L;
    private final ImageIcon gridppIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("GridPP_logo_trans_dark.png")));
    private final JLabel gridppLabel = new JLabel(gridppIcon);
    private final ImageIcon imperialIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("Imperial_logo_trans.png")));
    private final JLabel imperialLabel = new JLabel(imperialIcon);

    public AboutIFrame() {

        this.setClosable(true);
        this.setIconifiable(true);
        this.setMaximizable(false);
        this.setResizable(false);

        this.addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                RealTimeMonitor.nullAboutIFrame();
                // JM - the keyboard listener is added to the OpenGL canvas, return it there on close.
                RealTimeMonitor.getAf().getWwd().requestFocusInWindow();
            }
        });

        this.setTitle("About");

        JPanel panel = new JPanel(new BorderLayout());
        JPanel north = new JPanel(new BorderLayout());

        JPanel about = new JPanel(new BorderLayout());
        about.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("About"), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JTextArea abouttext = new JTextArea("");
        abouttext.setEditable(false);
        abouttext.setFocusable(false);
        abouttext.setOpaque(false);
        abouttext.append("The Real Time Monitor has been developed in the High Energy Physics e-Science group at Imperial College London since 2002.\nIt has undergone various incarnations, the latest being this OpenGL 3D Earth Java application.\nHomepage is http://gridportal.hep.ph.ic.ac.uk/rtm/");
        about.add(abouttext, BorderLayout.WEST);
        north.add(about, BorderLayout.NORTH);

        JPanel people = new JPanel();
        people.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("People"), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JTextArea peopletext = new JTextArea("");
        peopletext.setOpaque(false);
        peopletext.setEditable(false);
        peopletext.setFocusable(false);
        peopletext.append("Janusz Martyniak: developer since 2008\nMikhail Krypach: developer summer 2009\nSaguy Benaim: developer summer 2009\nGidon Moont: developer 2005-Feb 2008\nChristopher Eames: developer 2005\nMark Pesaresi: developer 2004\nStuart Wakefield: developer 2003\nSarah Marr: developer 2002\n");
        peopletext.append("\nDavid Colling: head of HEP e-Science group\n");
        peopletext.append("\nand thanks to NASA developers of WorldWind SDK, used in the 3D RTM");
        people.add(peopletext, BorderLayout.WEST);
        north.add(people, BorderLayout.SOUTH);

        panel.add(north, BorderLayout.NORTH);

        JPanel branding = new JPanel(new GridLayout(0, 2));
        branding.add(imperialLabel);
        branding.add(gridppLabel);
        branding.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(branding, BorderLayout.CENTER);

        JPanel controls = new JPanel(new BorderLayout());
        JButton ok = new JButton("OK");
        ok.setFocusable(false);
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AboutIFrame topParent = (AboutIFrame) (((JButton) e.getSource()).getRootPane().getParent());
                topParent.doDefaultCloseAction();
            }
        });
        controls.add(ok, BorderLayout.EAST);

        controls.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(controls, BorderLayout.SOUTH);

        this.add(panel);

        this.setOpaque(true);
        this.pack();
        //Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension sd = RealTimeMonitor.getAf().getSize();
        this.setLocation((sd.width - this.getWidth()) / 2, (sd.height - this.getHeight()) / 2);
        this.addKeyListener(new RTMKeyAdapter());        
//jm        RealTimeMonitor.getAf().getContentPane().add(this, JLayeredPane.PALETTE_LAYER);
        RealTimeMonitor.getAf().add(this, JLayeredPane.PALETTE_LAYER);
        this.setVisible(true);
        this.requestFocusInWindow();
    }
}
