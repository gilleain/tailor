package tailor.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelListener;

/**
 * @author maclean
 *
 */
public class ResultTable extends JPanel {
	
	private JTable table;
    private JLabel title;
	private RowBasedTableModel model;
	
	private JScrollPane scrollPane;
	
	public ResultTable() {
		this.model = new FileDataTableModel();
		this.table = new JTable(this.model);
        this.title = new JLabel("Results", JLabel.CENTER);
        
        this.table.setGridColor(Color.GRAY);
        this.table.setShowGrid(true);
        
		this.table.setColumnSelectionAllowed(true);
		this.setMultipleRowSelection();
//        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		this.scrollPane = new JScrollPane(this.table);
        
        this.setLayout(new BorderLayout());
        this.add(this.title, BorderLayout.NORTH);
		this.add(this.scrollPane, BorderLayout.CENTER);
	}
	
	public void setMultipleRowSelection() {
		this.table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}
	
	public void setSingleRowSelection() {
		this.table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	}
	
	public void setColumnSelectionOnly() {
		this.table.setColumnSelectionAllowed(true);
		this.table.setRowSelectionAllowed(false);
	}
	
	public void setRowSelectionOnly() {
		this.table.setColumnSelectionAllowed(false);
		this.table.setRowSelectionAllowed(true);
	}
	
	public void addListSelectionListener(ListSelectionListener listener) {
		this.table.getSelectionModel().addListSelectionListener(listener);
	}
	
	public void addColumnModelListener(TableColumnModelListener listener) {
		this.table.getColumnModel().addColumnModelListener(listener);
	}
    
    public void writeToFile(File file) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            RowBasedTableModel model = (RowBasedTableModel) this.table.getModel();
            String columnSeparator = "\t";
            int col = model.getColumnCount();
            int row = model.getRowCount();
            for (int h = 0; h < col; h++) {
                writer.write(model.getColumnName(h));
                writer.write(columnSeparator);
            }
            writer.newLine();
            
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    writer.write(String.valueOf(model.getValueAt(i, j)));
                    writer.write(columnSeparator);    
                }
                writer.newLine();
            }
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    public void setModel(RowBasedTableModel model) {
        this.model = model;
        this.table.setModel(model);
        //this.title.setText(model.getRowCount() + " Results");
    }
    
    public void setTitle() {
    	this.title.setText(this.model.getRowCount() + " Results");
    }
	
	public void selectColumns(int i, int j) {
		this.table.setColumnSelectionInterval(i, j);
		this.table.setRowSelectionInterval(0, this.table.getRowCount() - 1);
	}
    
    public void clearSelection() {
        this.table.clearSelection();
    }
    
    public void clearData() {
        this.model.clear();
        this.model.fireTableDataChanged();
        this.model.fireTableStructureChanged();
        this.title.setText("Results");
    }

    public void assignToCategory(Category category) {
        int columnStart = 2;    // to allow for the id columns
        int columnsToLookAt = category.getNumberOfFilters() * 2;
        int columnEnd = columnStart + columnsToLookAt;
        double[] values = new double[columnsToLookAt];
        for (int rowIndex = 0; rowIndex < this.table.getRowCount(); rowIndex++) {
            int i = 0;
            for (int columnIndex = columnStart; columnIndex < columnEnd; columnIndex += 2) {
                values[i] = Double.valueOf((String)this.table.getValueAt(rowIndex, columnIndex));
                values[i + 1] = Double.valueOf((String)this.table.getValueAt(rowIndex, columnIndex + 1));
                i += 2;
            }
            
            if (category.accept(values)) {
                String id = this.getFullIdForRow(rowIndex);
                category.addId(id);
            }
        }
    }
    
    public void selectRowsInCategory(Category category) {
        this.table.clearSelection();
        List<String> ids = category.getMemberIds();
        for (int rowIndex = 0; rowIndex < this.table.getRowCount(); rowIndex++) {
            String rowId = this.getFullIdForRow(rowIndex);
            for (String id : ids) {
                if (id.equals(rowId)) {
                    this.table.addRowSelectionInterval(rowIndex, rowIndex);
                    break;
                }
            }
        }
        this.table.setColumnSelectionInterval(0, this.table.getColumnCount() - 1);
    }
    
    public String getIdForRow(int row) {
    	 return (String) this.table.getValueAt(row, 0);
    }
    
    public String getMotifForRow(int row) {
    	return (String) this.table.getValueAt(row, 1);
    }
    
    public String getFullIdForRow(int row) {
        return ((String) this.table.getValueAt(row, 0)) + ((String) this.table.getValueAt(row, 1));
    }
	
	public List<String> getIdColumn() {
		List<String> idColumn = new ArrayList<>();
		int rowCount = this.table.getRowCount();
		for (int row = 0; row < rowCount; row++) {
			String id = this.getFullIdForRow(row);
			idColumn.add(id);
		}
		return idColumn;
	}
	
	public List<String>[] getSelectedColumnData() {
		int[] selectedColumnIndices = this.table.getSelectedColumns();
		int rowCount = this.table.getRowCount();
		@SuppressWarnings("unchecked")
        List<String>[] columns = new ArrayList[selectedColumnIndices.length];
		int i = 0;
		for (int index : selectedColumnIndices) {
			columns[i] = new ArrayList<>();
			for (int row = 0; row < rowCount; row++) {
				columns[i].add((String)this.table.getValueAt(row, index));
			}
			i++;
		}
		return columns;
	}
	
	public int[] getSelectedColumns() {
		return this.table.getSelectedColumns();
	}
	
	public int[] getSelectedRows() {
		return this.table.getSelectedRows();
	}
	
}
