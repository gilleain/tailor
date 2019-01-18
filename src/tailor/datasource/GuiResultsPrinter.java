package tailor.datasource;

import java.util.List;

import javax.swing.JProgressBar;

import tailor.app.FileDataTableModel;
import tailor.measurement.Measure;
import tailor.measurement.Measurement;

public class GuiResultsPrinter implements ResultsPrinter {
	
	private FileDataTableModel model;
	
	private JProgressBar progressBar;
	
	public GuiResultsPrinter(FileDataTableModel model) {
		this.model = model;
		this.progressBar = null;
	}
	
	public GuiResultsPrinter(FileDataTableModel model, JProgressBar progressBar) {
		this(model);
		this.progressBar = progressBar;
	}
	
	public void printHeader(List<Measure<? extends Measurement>> measures) {
		String[] headers = new String[measures.size() + 2];
		headers[0] = "pdbid";
		headers[1] = "motif";
		int h = 2;
		for (Measure<? extends Measurement> measure : measures) {
			headers[h] = measure.getName();
			h++;
		}
		
		this.model.setColumnIdentifiers(headers);
		this.model.fireTableStructureChanged();
	}

	public void printResult(Result result) {
		this.model.addRow(result);
		int rowCount = this.model.getRowCount();
		this.model.fireTableRowsInserted(rowCount - 1, rowCount);
	}

	public void signalNextStructure() {
		if (this.progressBar != null) {
			this.progressBar.setValue(this.progressBar.getValue() + 1);
		}
	}

}
