package tailor.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author maclean
 *
 */
public class SimpleTableModel extends RowBasedTableModel {

	private String[] headers;
	private ArrayList<String[]> data;

	public SimpleTableModel() {
		this.headers = null;
		this.data = new ArrayList<String[]>();
	}

	public SimpleTableModel(File file) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line;
		line = bufferedReader.readLine();
		String[] bits = line.split("\t");
		this.setColumnHeaders(bits);  
		while ((line = bufferedReader.readLine()) != null) {
			bits = line.split("\t");
			this.addRow(bits);
		}
		bufferedReader.close();
	}

	public void setColumnHeaders(String[] headers) {
		this.headers = headers;
	}

	public String getColumnName(int i) {
		if (this.headers != null) {
			return this.headers[i];
		} else {
			return "";
		}
	}

	public void addRow(String[] rowData) {
		this.data.add(rowData);
	}

	@Override
	public void clear() {
		this.data.clear();
	}

	@Override
	public int getColumnCount() {
		if (this.data.size() > 0) {
			return this.data.get(0).length;
		} else {
			return 0;
		}
	}

	@Override
	public int getRowCount() {
		return this.data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return this.data.get(rowIndex)[columnIndex];
	}

}
