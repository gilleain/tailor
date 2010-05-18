package tailor.app;

import javax.swing.table.AbstractTableModel;


public abstract class RowBasedTableModel extends AbstractTableModel {

    public abstract int getColumnCount();

    public abstract int getRowCount();

    public abstract Object getValueAt(int rowIndex, int columnIndex);
    
    public abstract void clear();
    
}
