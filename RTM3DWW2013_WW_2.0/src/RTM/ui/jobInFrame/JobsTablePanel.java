
package RTM.ui.jobInFrame ;

import RTM.datasource.glite.Job ;
import RTM.layers.Site;
import java.util.* ;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import javax.swing.table.* ;
import java.sql.Timestamp ;

/**
 * Create a sortable jobs table.
 * @see TableSorter
 * @see TableMap
 * @author Gidon Moont
 */
public class JobsTablePanel extends JPanel
{

  private static final long serialVersionUID = 15051973L ;

  private Hashtable GridJobs = null ;

  /**
   *
   */
  public JScrollPane tablePane ;

  private JobsTableModel model ;
  private TableSorter sorter ;
  private JTable table ;

  /**
   * Creates a sortable jobs table initialised with the jobs contained in
   * the jobs (currently LRU) buffer. See {@link Site}, which maintains it's jobs in
   * a fixed size LRU cache, so the oldest jobs are removed first.
   * @param GridJobs jobs buffer
   */
  public JobsTablePanel( Hashtable GridJobs )
  {

    this.GridJobs = GridJobs ;

    setBackground( Color.white ) ;
    setLayout( new BorderLayout() ) ;

    final String[] columnNames = { "jobID" , "State" , "Time" , "RB" , "CE" , "Queue" , "UI" , "VO" , "Registered" } ;

    model = new JobsTableModel( columnNames , 0 ) ;

    sorter = new TableSorter( model ) ;

    table = new JTable((TableModel) sorter) ;
    table.setShowVerticalLines( true ) ;
    table.setShowHorizontalLines( true ) ;
    table.setOpaque( true ) ;
    table.setGridColor( Color.cyan ) ;
    table.setSelectionBackground( Color.cyan ) ;
    table.setForeground( Color.black ) ;
    table.setSelectionForeground( Color.black ) ;
    table.setIntercellSpacing( new Dimension( 6 , 1 ) ) ;

    table.setFont( new Font( "Monospaced" , Font.PLAIN , 10 ) ) ;

    this.setPreferredSize( new Dimension( 850 , 400 ) ) ;

    ArrowedButtonHeaderRenderer renderer = new ArrowedButtonHeaderRenderer() ;
    TimestampColumnRenderer tsrenderer = new TimestampColumnRenderer() ;

    TableColumnModel columnmodel = table.getColumnModel() ;
    for( int i = 0 ; i < table.getColumnCount() ; i ++ )
    {
      columnmodel.getColumn(i).setHeaderRenderer( renderer ) ;
    }
    
    columnmodel.getColumn(2).setCellRenderer( tsrenderer ) ;
    columnmodel.getColumn(8).setCellRenderer( tsrenderer ) ;

    columnmodel.getColumn(0).setPreferredWidth( 160 ) ;
    columnmodel.getColumn(1).setPreferredWidth( 90 ) ;
    columnmodel.getColumn(2).setPreferredWidth( 130 ) ;
    columnmodel.getColumn(8).setPreferredWidth( 130 ) ;

    JTableHeader tableHeader = table.getTableHeader() ;

    // Listeners
    sorter.addMouseListenerToHeaderInTable( table ) ;
    tableHeader.addMouseListener( new HeaderListener( tableHeader , renderer ) ) ;

    tablePane = new JScrollPane( table ) ;
    // tablePane.setBorder( BorderFactory.createTitledBorder( "Information on all Jobs on currently monitored components" ) ) ;
    this.add( tablePane , BorderLayout.CENTER ) ;

  } // Closes constructor

  /**
   * Populates the table with jobs.
   */
  public void populateTable()
  {

    while( model.getRowCount() > 0 )
    {
      model.removeRow( 0 ) ;
    }
    sorter.reSortAfterChange() ;

    synchronized( GridJobs )
    {
      for( Enumeration G =  GridJobs.keys() ; G.hasMoreElements() ; )
      {
        String jobid = (String) G.nextElement() ;
        Job job = (Job) GridJobs.get( jobid ) ;
        String ce = job.getCE() ;
        String state = job.getState() ;
        Timestamp timestamp = job.getUpdate() ;
        String rb = job.getRB() ;
        String queue = job.getQueue() ;
        String ui = job.getUI() ;
        String vo = job.getVO() ;
        Timestamp registration = job.getRegistered() ;

        model.addRow( new Object[]{ jobid , state , timestamp , rb , ce , queue , ui , vo , registration } ) ;
      }
    }

    sorter.reSortAfterChange() ;

  } // Closes method





  class HeaderListener extends MouseAdapter
  {
    JTableHeader   header ;
    ArrowedButtonHeaderRenderer renderer ;

    private HeaderListener( JTableHeader header , ArrowedButtonHeaderRenderer renderer )
    {
      this.header   = header;
      this.renderer = renderer;
    }

        @Override
    public void mousePressed(MouseEvent e)
    {
      int col = header.columnAtPoint(e.getPoint()) ;
      int sortCol = header.getTable().convertColumnIndexToModel(col) ;
      renderer.setPressedColumn(col) ;
      renderer.setSelectedColumn(col) ;
      header.repaint();

      boolean isAscent;
      if( ArrowedButtonHeaderRenderer.DOWN == renderer.getState(col) )
      {
        isAscent = true ;
      } else {
        isAscent = false ;
      }
      sorter.reSortAfterChange() ;

    }

        @Override
    public void mouseReleased(MouseEvent e)
    {
      int col = header.columnAtPoint(e.getPoint()) ;
      renderer.setPressedColumn(-1) ;                // clear
      header.repaint() ;
    }

  } // end subclass

}
