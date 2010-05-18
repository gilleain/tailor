package tailor.msdmotif;

import java.util.ArrayList;

import tailor.app.RowBasedTableModel;

public class MSDMotifResultTableModel extends RowBasedTableModel {
	
	private ArrayList<MSDMotifResult> msdMotifResults;
	
	public MSDMotifResultTableModel() {
		this.msdMotifResults = new ArrayList<MSDMotifResult>();
	}
	
	public void addResult(MSDMotifResult msdMotifResult) {
		this.msdMotifResults.add(msdMotifResult);
		this.fireTableDataChanged();
	}
	
	public void clear() {
		this.msdMotifResults.clear();
		this.fireTableDataChanged();
	}

	public int getColumnCount() {
		return 9;
	}

	public int getRowCount() {
		if (this.msdMotifResults.size() < 30) {
			return 40;
		} else {
			return this.msdMotifResults.size();
		}
	}

	public Object getValueAt(int row, int column) {
		if (this.msdMotifResults.size() == 0 || row > this.msdMotifResults.size()) {
			return null;
		} else {
			return this.msdMotifResults.get(row).get(column);
		}
	}
	
	public String getColumnName(int i) {
		return MSDMotifResult.getFieldNameByNumber(i);
	}

}
