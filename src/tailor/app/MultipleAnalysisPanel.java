package tailor.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

public class MultipleAnalysisPanel extends JPanel 
	implements ActionListener, ListSelectionListener, TableColumnModelListener {
	
	public static String JMOL_TAG = "JMOL_PANEL";
	public static String RAMA_TAG = "RAMA_PANEL";
	
	private String currentTag;
	
	private CardLayout cardLayout;
	
	private ResultTable resultTable;
	
	private JPanel innerPanel;
	
	private JPanel buttonPanel;
	
	private JToggleButton jMolButton;
	
	private JToggleButton ramaButton;
	
	private JmolPanel jMolPanel;
	
	private RamachandranPlotPanel plotPanel;
	
	public MultipleAnalysisPanel(ResultTable resultTable) {
		
		this.resultTable = resultTable;
		this.resultTable.addListSelectionListener(this);
		this.resultTable.addColumnModelListener(this);
		
		this.innerPanel = new JPanel();
		this.buttonPanel = new JPanel();
		
		this.cardLayout = new CardLayout(); 
		this.innerPanel.setLayout(this.cardLayout);
		
		this.plotPanel = new RamachandranPlotPanel();
		this.innerPanel.add(this.plotPanel, MultipleAnalysisPanel.RAMA_TAG);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		this.ramaButton = new JToggleButton("RAMA");
		this.ramaButton.setActionCommand("RAMA");
		this.ramaButton.addActionListener(this);
		buttonGroup.add(this.ramaButton);
		this.ramaButton.setSelected(true);
		this.buttonPanel.add(this.ramaButton);
	
		try {
			Class.forName("org.jmol.api.JmolViewer");
			
			// XXX passing in a null is nasty, but allows everything to still work
			this.jMolPanel = new JmolPanel(null);
			this.innerPanel.add(this.jMolPanel, MultipleAnalysisPanel.JMOL_TAG);
			
			this.jMolButton = new JToggleButton("JMOL");
			this.jMolButton.setActionCommand("JMOL");
			this.jMolButton.addActionListener(this);
			buttonGroup.add(this.jMolButton);
			this.buttonPanel.add(this.jMolButton);
		} catch (ClassNotFoundException cnfe) {
			this.jMolPanel = null;
		}
		
		this.setLayout(new BorderLayout());
		this.add(this.buttonPanel, BorderLayout.NORTH);
		this.add(this.innerPanel, BorderLayout.CENTER);
		
		this.activateRamaPanel();
	}
	
	private void activateRamaPanel() {
		this.currentTag = MultipleAnalysisPanel.RAMA_TAG;
		this.resultTable.setMultipleRowSelection();
		this.resultTable.setColumnSelectionOnly();
		this.showPanel(this.currentTag);
	}
	
	private void activateJMolPanel() {
		this.currentTag = MultipleAnalysisPanel.JMOL_TAG;
		this.resultTable.setSingleRowSelection();
		this.resultTable.setRowSelectionOnly();
		this.showPanel(this.currentTag);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("RAMA")) {
			this.activateRamaPanel();
		} else if (command.equals("JMOL")) {
			this.activateJMolPanel();
		}
	}
	
	private void updateJMolPanel() {
		// this guard is slightly redundant, since this method 
		// should not be called if the jmol panel is not loaded
		if (this.jMolPanel != null) {
			int[] selectedRows = this.resultTable.getSelectedRows();
			if (selectedRows.length > 0) {
				int first = selectedRows[0];	// XXX
				try {
					String id = this.resultTable.getIdForRow(first);
					this.jMolPanel.loadStructure(id);
					
					String motif = this.resultTable.getMotifForRow(first);
					this.jMolPanel.selectMotif(motif);
				} catch (ArrayIndexOutOfBoundsException a) {
					System.out.println(a);
				}
			}
		}
	}
	
	public void showPanel(String tag) {
		this.cardLayout.show(this.innerPanel, tag);
	}
	
	private void updateRamaPanel() {
		int[] selectedColumns = this.resultTable.getSelectedColumns();
		for (int i : selectedColumns) {
			System.out.print(i);
			System.out.print(", ");
		}
		System.out.println();
		if (selectedColumns.length > 1) {
			ArrayList<String> ids = this.resultTable.getIdColumn();
			ArrayList[] data = this.resultTable.getSelectedColumnData();
			this.plotPanel.addColumns(ids, data);
		}
	}
	
	public void clear() {
		if (this.jMolPanel != null) {
			this.jMolPanel.clear();
		}
		this.plotPanel.clearPoints();
	}
	
	public Image getPlotImage() {
		return this.plotPanel.getImage();
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if (this.currentTag.equals(MultipleAnalysisPanel.JMOL_TAG)) {
			this.updateJMolPanel();
		} else if (this.currentTag.equals(MultipleAnalysisPanel.RAMA_TAG)) {
//			this.updateRamaPanel();		// XXX : this should be handled in columnSelection!
		}
	}

	public void columnAdded(TableColumnModelEvent e) {
		
	}

	public void columnMarginChanged(ChangeEvent e) {
		
	}

	public void columnMoved(TableColumnModelEvent e) {
		
	}

	public void columnRemoved(TableColumnModelEvent e) {
		
	}

	public void columnSelectionChanged(ListSelectionEvent e) {
		this.updateRamaPanel();		// XXX assuming that this only happens for rama! 
	}

}
