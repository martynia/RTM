
package RTM.ui.jobInFrame;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

class TimestampColumnRenderer extends JLabel implements TableCellRenderer
{

  private static final long serialVersionUID = 15051973L ;

  private java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ;
  public TimestampColumnRenderer()
  {
    super() ;
    this.setFont( new Font( "Monospace" , Font.PLAIN , 10 ) ) ;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
                   boolean isSelected, boolean hasFocus, int row, int column)
  {
    JLabel label = this ;
    label.setText( timeFormat.format ( ( (java.sql.Timestamp)value ).getTime() ) ) ;
    return label ;
  }

}
