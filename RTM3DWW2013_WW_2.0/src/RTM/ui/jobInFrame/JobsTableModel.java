
package RTM.ui.jobInFrame;

import javax.swing.table.DefaultTableModel;

class JobsTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 15051973L;
    private String[] columnNames = null;

    public JobsTableModel(String[] columnNames, int rowCount) {
        super(columnNames, rowCount);
        this.columnNames = columnNames;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return super.getValueAt(row, col);
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    // types of columns
    public Class getColumnClass(int col) {
        switch (col) {
            case 2:
                return java.sql.Timestamp.class;
            case 8:
                return java.sql.Timestamp.class;
            default:
                return String.class;
        }
    }

    public void setValueAt(Object obj, int row, int col) {
        switch (col) {
            default:
                super.setValueAt(obj, row, col);
                return;
        }
    }
}
