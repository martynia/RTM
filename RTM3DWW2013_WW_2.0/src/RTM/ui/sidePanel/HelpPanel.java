/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.ui.sidePanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Janusz Martyniak
 */
class HelpPanel extends JPanel{
  private static final long serialVersionUID = 15051973L ;

  public HelpPanel()
  {
    super() ;

    JPanel panel = new JPanel( new BorderLayout() ) ;
    panel.setBorder( new EmptyBorder( 10 , 10 , 10 , 10 ) ) ;

    JPanel key = new JPanel() ;
    key.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder( "Information" ) , BorderFactory.createEmptyBorder( 10 , 10 , 10 , 10 ) ) )  ;
    JTextArea keytext = new JTextArea( "" ) ;
    // keytext.setLineWrap( true ) ;
    // keytext.setWrapStyleWord( true ) ;
    // keytext.setColumns( 100 ) ;

    keytext.append( "Circles represent Computing Elements at the Sites,\nthe fractions of jobs in given states are shown as...\n" ) ;
    //keytext.setForeground(Color.MAGENTA);
    keytext.append( "Magenta"); 
    //keytext.setForeground(Color.BLACK);
    keytext.append( " = Scheduled\n" ) ;
    keytext.append( "Green = Running\n" ) ;
    keytext.append( "and the size the circle pulsates to is proportional\nto the log of the number of jobs running.\n\n" ) ;

    keytext.append( "Triangles represent (monitored) Resource Brokers\nor WMS at the Sites.\n\n" ) ;

    keytext.append( "Lines represent jobs being transfered between Sites.\n" ) ;
    keytext.append( "Magenta lines show jobs being scheduled\nto a Computing Element.\n" ) ;
    keytext.append( "Yellow lines show Done (Success) jobs being returned\nto their Resource Broker or WMS.\n" ) ;
    keytext.append( "Red lines show Aborted or Cancelled jobs being returned\nto their Resource Broker or WMS.\n\n" ) ;

    keytext.append( "The changes displayed are shown in Real Time -3 minutes.\nThis is the time shown by the clock at the bottom left of the screen." ) ;

    keytext.setOpaque( false ) ;
    keytext.setSize( keytext.getPreferredSize() ) ;
    key.add( keytext ) ;
    panel.add( key , BorderLayout.NORTH ) ;

    JPanel how = new JPanel( new BorderLayout() ) ;
    how.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder( "Controls" ) , BorderFactory.createEmptyBorder( 10 , 10 , 10 , 10 ) ) )  ;
    JTextArea howtext = new JTextArea( "" ) ;

    howtext.append( "H - brings up this screen\n" ) ;
    howtext.append( "A - brings up the 'About' screen\n" ) ;
    howtext.append( "V - Virtual Organisation selection screen\n" ) ;
    howtext.append( "M - Menubar on/off (default on)\n" ) ;
    howtext.append( "N - Number of jobs on/off (default on)\n\n" ) ;
    howtext.append( "arrow keys or a dragged clicked mouse button will move\n the Earth around\n" ) ;
    howtext.append( "+/- - zooms the view in/out\n" ) ;
    howtext.append( "T - on/off toggle for the Earth turning (t.b.i.)\n" ) ;
    howtext.append( "C - clear all current windows (including iconised windows)\n" ) ;
    //howtext.append( "numbers 0 through 9 are preset views\n\n" ) ;
    howtext.append( " Command line switches:\n");
    howtext.append( " -Drtm.noStats - no job statistics at startup\n");
    howtext.append( " -Drtm.noAbout - no About greeting window at startup\n");
    howtext.append( " -Drtm.minLon -Drtm.maxLon -Drtm.minLat -Drtm.maxLat - initial zoom sector (degrees)\n");
    howtext.append( "\n");
    howtext.append( "The table of jobs that is accesible through the sites windows\n is sortable - click on the column headers to sort\n\n" ) ;
    howtext.append( "Closing the main window will exit the program" ) ;

    howtext.setOpaque( false ) ;
    howtext.setSize( howtext.getPreferredSize() ) ;
    how.add( howtext , BorderLayout.WEST ) ;
    panel.add( how , BorderLayout.CENTER ) ;
    this.add( panel ) ;

    this.setOpaque( true ) ;

  }

}
