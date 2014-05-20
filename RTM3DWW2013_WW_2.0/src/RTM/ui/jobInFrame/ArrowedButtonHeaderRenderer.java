
package RTM.ui.jobInFrame;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

class ArrowedButtonHeaderRenderer extends JButton implements TableCellRenderer
{

  private static final long serialVersionUID = 15051973L ;

  public static final Integer NONE = new Integer(0) ;
  public static final Integer DOWN = new Integer(1) ;
  public static final Integer UP = new Integer(2) ;

  Hashtable<Integer,Integer> state ;
  JButton downButton , upButton ;

  int pushedColumn ;

  public ArrowedButtonHeaderRenderer()
  {
    pushedColumn   = -1;
    state = new Hashtable<Integer,Integer>();

    setMargin(new Insets(0,0,0,0));
    setHorizontalTextPosition(LEFT) ;
    setBorder( BorderFactory.createEmptyBorder( 1 , 1 , 1 , 1 ) ) ;
    setBorder( BorderFactory.createMatteBorder( 0 , 1 , 0 , 1 , Color.white ) ) ;
    setIconTextGap( 2 ) ;
    setBackground( Color.cyan ) ;
    setIcon( new BlankIcon() ) ;

    downButton = new JButton();
    downButton.setMargin(new Insets(0,0,0,0));
    downButton.setHorizontalTextPosition(LEFT);
    downButton.setBorder( BorderFactory.createMatteBorder( 0 , 1 , 0 , 1 , Color.white ) ) ;
    downButton.setIconTextGap( 2 ) ;
    downButton.setBackground( Color.cyan ) ;
    downButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
    downButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

    upButton = new JButton();
    upButton.setMargin(new Insets(0,0,0,0));
    upButton.setHorizontalTextPosition(LEFT);
    upButton.setBorder( BorderFactory.createEmptyBorder( 1 , 1 , 1 , 1 ) ) ;
    upButton.setBorder( BorderFactory.createMatteBorder( 0 , 1 , 0 , 1 , Color.white ) ) ;
    upButton.setIconTextGap( 2 ) ;
    upButton.setBackground( Color.cyan ) ;
    upButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
    upButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));

  }

  public Component getTableCellRendererComponent(JTable table, Object value,
                   boolean isSelected, boolean hasFocus, int row, int column)
  {
    JButton button = this;
    Integer obj = state.get(new Integer(column));
    if (obj != null) {
      if (obj.intValue() == DOWN) {
        button = downButton;
      } else {
        button = upButton;
      }
    }
    button.setText((value ==null) ? "" : value.toString());
    button.setToolTipText((value ==null) ? "" : value.toString());
    boolean isPressed = (column == pushedColumn);
    button.getModel().setPressed(isPressed);
    button.getModel().setArmed(isPressed);
    return button;
  }
  
  public void setPressedColumn(int col) {
    pushedColumn = col;
  }
  
  public void setSelectedColumn(int col) {
    if (col < 0) return;
    Integer value = null;
    Integer obj = state.get(new Integer(col));
    if (obj == null) {
      value = DOWN ;
    } else {
      if( obj == DOWN ) {
        value = UP ;
      } else {
        value = DOWN ;
      }
    }
    state.clear();
    state.put(new Integer(col), value);
  } 
  
  public int getState(int col) {
    Integer retValue;
    Object obj = state.get(new Integer(col));
    if (obj == null) {
      retValue = NONE;
    } else {
      if (obj == DOWN) {
        retValue = DOWN;
      } else {
        retValue = UP;
      }
    }
    return retValue.intValue() ;
  } 

  public class BlankIcon implements Icon
  {

    private int size ;

    public BlankIcon()
    {
      this.size = size ;    
    }

    public void paintIcon( Component c , Graphics g , int x , int y )
    {
    }

    public int getIconWidth()
    {
      return size ;
    }

    public int getIconHeight()
    {
      return size ;
    }

  }

  public class BevelArrowIcon implements Icon
  {
    public static final int UP    = 0;         // direction
    public static final int DOWN  = 1;
  
    private static final int DEFAULT_SIZE = 11;

    private Color edge1;
    private Color edge2;
    private Color fill;
    private int size;
    private int direction;

    public BevelArrowIcon(int direction, boolean isRaisedView, boolean isPressedView) {
      if (isRaisedView) {
        if (isPressedView) {
          init( UIManager.getColor("controlLtHighlight"),
                UIManager.getColor("controlDkShadow"),
                UIManager.getColor("controlShadow"),
                DEFAULT_SIZE, direction);
        } else {
          init( UIManager.getColor("controlHighlight"),
                UIManager.getColor("controlShadow"),
                UIManager.getColor("control"),
                DEFAULT_SIZE, direction);
        }
      } else {
        if (isPressedView) {
          init( UIManager.getColor("controlDkShadow"),
                UIManager.getColor("controlLtHighlight"),
                UIManager.getColor("controlShadow"),
                DEFAULT_SIZE, direction);
        } else {
          init( UIManager.getColor("controlShadow"),
                UIManager.getColor("controlHighlight"),
                UIManager.getColor("control"),
                DEFAULT_SIZE, direction);
        }
      }
    }

    public BevelArrowIcon(Color edge1, Color edge2, Color fill,
                     int size, int direction) {
      init(edge1, edge2, fill, size, direction);
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      switch (direction) {
        case DOWN: drawDownArrow(g, x, y); break;
        case   UP: drawUpArrow(g, x, y);   break;
      }
    }

    public int getIconWidth() {
      return size;
    }

    public int getIconHeight() {
      return size;
    }

    private void init(Color edge1, Color edge2, Color fill,
                     int size, int direction) {
      this.edge1 = edge1;
      this.edge2 = edge2;
      this.fill = fill;
      this.size = size;
      this.direction = direction;
    }

    private void drawDownArrow(Graphics g, int xo, int yo) {
      g.setColor(edge1);
      g.drawLine(xo, yo,   xo+size-1, yo);
      g.drawLine(xo, yo+1, xo+size-3, yo+1);
      g.setColor(edge2);
      g.drawLine(xo+size-2, yo+1, xo+size-1, yo+1);
      int x = xo+1;
      int y = yo+2;
      int dx = size-6;      
      while (y+1 < yo+size) {
        g.setColor(edge1);
        g.drawLine(x, y,   x+1, y);
        g.drawLine(x, y+1, x+1, y+1);
        if (0 < dx) {
          g.setColor(fill);
          g.drawLine(x+2, y,   x+1+dx, y);
          g.drawLine(x+2, y+1, x+1+dx, y+1);
        }
        g.setColor(edge2);
        g.drawLine(x+dx+2, y,   x+dx+3, y);
        g.drawLine(x+dx+2, y+1, x+dx+3, y+1);
        x += 1;
        y += 2;
        dx -= 2;     
      }
      g.setColor(edge1);
      g.drawLine(xo+(size/2), yo+size-1, xo+(size/2), yo+size-1); 
    }

    private void drawUpArrow(Graphics g, int xo, int yo) {
      g.setColor(edge1);
      int x = xo+(size/2);
      g.drawLine(x, yo, x, yo); 
      x--;
      int y = yo+1;
      int dx = 0;
      while (y+3 < yo+size) {
        g.setColor(edge1);
        g.drawLine(x, y,   x+1, y);
        g.drawLine(x, y+1, x+1, y+1);
        if (0 < dx) {
          g.setColor(fill);
          g.drawLine(x+2, y,   x+1+dx, y);
          g.drawLine(x+2, y+1, x+1+dx, y+1);
        }
        g.setColor(edge2);
        g.drawLine(x+dx+2, y,   x+dx+3, y);
        g.drawLine(x+dx+2, y+1, x+dx+3, y+1);
        x -= 1;
        y += 2;
        dx += 2;     
      }
      g.setColor(edge1);
      g.drawLine(xo, yo+size-3,   xo+1, yo+size-3);
      g.setColor(edge2);
      g.drawLine(xo+2, yo+size-2, xo+size-1, yo+size-2);
      g.drawLine(xo, yo+size-1, xo+size, yo+size-1);
    }

  }

}
