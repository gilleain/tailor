package tailor.app;

import java.util.ArrayList;
import java.util.List;

import tailor.datasource.Result;


/**
 * @author maclean
 *
 */
public class FileDataTableModel extends RowBasedTableModel {
    
    private String[] columnHeaders;
    private List<Result> data;
    private int numberOfColumns;
    
    public FileDataTableModel() {
    	this(0);
    }
    
    public FileDataTableModel(int numberOfColumns) {
        this.data = new ArrayList<>();
        this.numberOfColumns = numberOfColumns;
        this.columnHeaders = null;
    }

    public FileDataTableModel(List<Result> data) {
        this.data = data;
        if (data.size() > 0) {
        	// XXX : while simple, this could be dangerous if the file format is wrong. 
            this.numberOfColumns = data.get(0).getNumberOfColumns();
        } else {
            this.numberOfColumns = 0;
        }
        this.columnHeaders = null;
    }
    
    public void addRow(Result result) {
    	this.data.add(result);
    }

    public int getColumnCount() {
        return this.numberOfColumns;
    }

    public int getRowCount() {
        return this.data.size();
    }
    
    public Object getValueAt(int row, int col) {
        if (row < this.data.size()) {
            return this.data.get(row).getValueAtColumn(col);
        } else {
            return null;
        }
    }
    
    public String getColumnName(int i) {
        if (this.columnHeaders == null) {
        	return "A"; //?? FIXME : columnHeaders should never be null?
        } else {
            return this.columnHeaders[i];
        }
    }
    
    public void clear() {
        this.data.clear();
    }
    
    public void setColumnIdentifiers(String[] columnIdentifiers) {
        this.columnHeaders = columnIdentifiers;
    }
    
}
