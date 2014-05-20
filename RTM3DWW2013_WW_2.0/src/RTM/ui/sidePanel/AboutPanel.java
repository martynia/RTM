/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.sidePanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Style;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * The About.. screen.
 * @author Mikhail Khrypach
 */
public class AboutPanel extends JPanel {

    private static final long serialVersionUID = 150519732009L;
    private final ImageIcon gridppIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("GridPP_logo_trans_dark.png")));
    private final JLabel gridppLabel = new JLabel(gridppIcon);
    private final ImageIcon imperialIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("Imperial_logo_trans.png")));
    private final JLabel imperialLabel = new JLabel(imperialIcon);
    private final ImageIcon rtmIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("EST-RTM-LogoFondNoirTr.png")));
    private final JLabel rtmLabel = new JLabel("3D Real Time Monitor     ", rtmIcon, javax.swing.SwingConstants.CENTER);

    ;                                                       

    /**
     * Default constructor.
     */
    public AboutPanel() {
        super(new BorderLayout());

        JPanel rtmLogoPannel = new JPanel(new BorderLayout());
        rtmLabel.setFont(new Font("SansSerif", Font.PLAIN, 30));
        rtmLogoPannel.add(rtmLabel);



        JPanel north = new JPanel(new BorderLayout());
        JPanel about = new JPanel(new BorderLayout());
        about.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("About"), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
//        
//        (http://www.javakb.com/Uwe/Forum.aspx/java-gui/6365/adding-a-active-URL-link-in-jtextArea)
//        JEditorPane editorPane = new JEditorPane("text/html", "");
//        StyleSheet css = ((HTMLEditorKit) editorPane.getEditorKit()).getStyleSheet();
//        Style style = css.getStyle("body");
//        editorPane.setText("<html><body>Test <a href='http://www.java.net'>");
        
        JTextArea abouttext = new JTextArea("");
        
//        editorPane.setText(abouttext.getText());
//        editorPane.setEditable(false);
        
        //abouttext.setPreferredSize(new Dimension(this.getHeight()/4,this.getWidth()));
        abouttext.setEditable(false);
        abouttext.setFocusable(false);
        abouttext.setOpaque(false);
        abouttext.setLineWrap(true);
        abouttext.setWrapStyleWord(true);
        abouttext.append("The RTM is a visualisation of activity on the grid computing infrastructure. It has been developed in the High Energy Physics e-Science group at Imperial College London since 2002.");
        abouttext.append("Initially funded by the GridPP collaboration, it is now a part of the EC-funded e-ScienceTalk project.\n");
        abouttext.append("The latest version utilises the NASA World Wind virtual globe which is based on OpenGL and Java.\n"
                + "\nThe RTM overlays the movement of site activity and job transfers on to the 3D globe giving users the to see" + ""
                + "the current state of the grid infrastructure.\n\n");
        abouttext.append("To learn more about the RTM please visit the website http://rtm.hep.ph.ic.ac.uk. This includes information for site administrators who wish to add their site to the RTM.\n\n");
        abouttext.append("There is also a mailing list for users that you can sighn up for here:http://mailman.ic.ac.uk/mailman/listinfo/rtm-users\n\n");
        abouttext.append(" We hope you enjoy using the RTM, any feedback or suggestions can be sent to lcg-monitor@imperial.ac.uk");
        about.add(abouttext, BorderLayout.CENTER);
        //todo about.add(editorPane,BorderLayout.CENTER);
        
        north.add(about, BorderLayout.NORTH);
        JTextArea hinttext = new JTextArea("");
        hinttext.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Hint .."), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        hinttext.setEditable(false);
        hinttext.setFocusable(false);
        hinttext.setOpaque(false);
        hinttext.setForeground(Color.red);
        hinttext.append("Type 'h' (with the globe in focus) to bring up the help panel ");
        hinttext.setPreferredSize(new Dimension(this.getHeight() / 5, this.getWidth()));
        north.add(hinttext, BorderLayout.CENTER);
        JPanel people = new JPanel();
        people.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("People"), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JTextArea peopletext = new JTextArea("");
        peopletext.setOpaque(false);
        peopletext.setEditable(false);
        peopletext.setFocusable(false);
        peopletext.append("Janusz Martyniak: developer since 2008\nMikhail Krypach developer summer 2009\nSaguy Benaim: developer summer 2009\nGidon Moont: developer 2005-Feb 2008\nChristopher Eames: developer 2005\nMark Pesaresi: developer 2004\nStuart Wakefield: developer 2003\nSarah Marr: developer 2002\n");
        peopletext.append("\nDavid Colling: head of HEP e-Science group\n");
        peopletext.append("\nand thanks to NASA developers of WorldWind SDK, used in the 3D RTM");
        people.add(peopletext, BorderLayout.WEST);
        north.add(people, BorderLayout.SOUTH);

        this.add(rtmLogoPannel, BorderLayout.NORTH);
        this.add(north, BorderLayout.CENTER);

        JPanel branding = new JPanel(new GridLayout(0, 2));
        branding.add(imperialLabel);
        branding.add(gridppLabel);
        branding.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add(branding, BorderLayout.SOUTH);
        this.setVisible(true);
    }
}
