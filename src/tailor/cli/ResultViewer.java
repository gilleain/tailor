package tailor.cli;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.cli.ParseException;

import tailor.app.JmolPanel;
import tailor.app.ResultTable;
import tailor.app.SimpleTableModel;

public class ResultViewer {
	
	private static class TableFrame extends JFrame implements ListSelectionListener {
		
		private ResultTable resultTable;
		
		private JmolPanel jmolPanel;
		
		public TableFrame(String resultsFileName, String structureSourceFileName) throws IOException {
			super("Results");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			this.resultTable = new ResultTable();
			this.resultTable.setModel(new SimpleTableModel(new File(resultsFileName)));
			this.resultTable.addListSelectionListener(this);
			add(resultTable, BorderLayout.WEST);
			
			this.jmolPanel = new JmolPanel(structureSourceFileName);
			add(this.jmolPanel, BorderLayout.EAST);
			
			pack();
			setLocationRelativeTo(null);
		}

		@Override
		public void valueChanged(ListSelectionEvent listSelectionEvent) {
			int[] selectedRows = this.resultTable.getSelectedRows();
			if (selectedRows.length > 0) {
				int first = selectedRows[0];	// XXX
				try {
					String id = this.resultTable.getIdForRow(first);
					id = id.substring(0, 4);	// TODO
//					String motifString = this.resultTable.getMotifForRow(first);	// XXX
					String motifString = this.resultTable.getIdForRow(first);
					ParsedMotif motif = parse(motifString);
					this.jmolPanel.loadStructure(id);
					this.jmolPanel.selectMotif(motif.chainName(), motif.start(), motif.end());
				} catch (ArrayIndexOutOfBoundsException a) {
					System.out.println(a);
				}
			}
		}
		
		private ParsedMotif parse(String motif) {
			System.err.println("Parsing " + motif);
			String chainName = motif.substring(0, 1);
			int firstBracket = motif.indexOf('(');
			String[] groupStrings = motif.substring(firstBracket + 1).split("\\|");
			System.err.println(Arrays.toString(groupStrings));
			if (groupStrings.length == 1) {
				String groupNumber = parseGroupNumber(groupStrings[0]);
				return new ParsedMotif(chainName, groupNumber, groupNumber);
			} else {
				String start = parseGroupNumber(groupStrings[0]);
				String end = parseGroupNumber(groupStrings[groupStrings.length - 1]);
				return new ParsedMotif(chainName, start, end);
			}
		}
		
		private String parseGroupNumber(String groupString) {
			// XXX assumes 3-char group name!
			return groupString.substring(3, groupString.indexOf('/')); 
		}
		
		private record ParsedMotif(String chainName, String start, String end) {}

	}
	
	public static void main(String[] args) throws ParseException, IOException {
		CommandLineHandler handler = new CommandLineHandler().processArguments(args);
		if (handler.getResultsFileName().isPresent() && handler.getStructureSourceFileName().isPresent()) {
			TableFrame frame = new TableFrame(
					handler.getResultsFileName().get(),
					handler.getStructureSourceFileName().get());
			SwingUtilities.invokeLater(() -> frame.setVisible(true));
		} else {
			System.err.println("Please provide a results file");
		}
	}

}
