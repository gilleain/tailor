package tailor.cli;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.cli.ParseException;

import tailor.app.ResultTable;
import tailor.app.SimpleTableModel;

public class ResultViewer {
	
	private static class TableFrame extends JFrame {
		private ResultTable resultTable;
		
		public TableFrame(String resultsFileName) throws IOException {
			super("Results");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			resultTable = new ResultTable();
			this.resultTable.setModel(new SimpleTableModel(new File(resultsFileName)));
			add(resultTable);
			
			
			pack();
			setLocationRelativeTo(null);
		}
	}
	
	public static void main(String[] args) throws ParseException, IOException {
		CommandLineHandler handler = new CommandLineHandler().processArguments(args);
		if (handler.getResultsFileName().isPresent()) {
			TableFrame frame = new TableFrame(handler.getResultsFileName().get());
			SwingUtilities.invokeLater(() -> frame.setVisible(true));
		} else {
			System.err.println("Please provide a results file");
		}
	}

}
