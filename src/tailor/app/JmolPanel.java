package tailor.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;

public class JmolPanel extends JPanel implements ActionListener, ListSelectionListener {
	
	private JmolViewer viewer;
	
	private ResultTable resultTable;
	
	private String currentlyLoadedId;
	private String currentlySelectedMotif;
	private String currentSelection;
	
	private String displayScriptFragment = "wireframe 30; spacefill 40;";
	
	private File structureDirectory;
    
	public JmolPanel(ResultTable resultTable) {
      this.setLayout(new BorderLayout());
      
      JToolBar controlPanel = new JToolBar();
      controlPanel.setLayout(new GridLayout(2, 3));
      controlPanel.setFloatable(false);
      
      JToggleButton sidechainToggle = new JToggleButton("Sidechains");
      sidechainToggle.setActionCommand("SIDECHAIN TOGGLE");
      sidechainToggle.addActionListener(this);
      controlPanel.add(sidechainToggle);
      
      JToggleButton backboneHBondsToggle = new JToggleButton("Backbone HBonds");
      backboneHBondsToggle.setActionCommand("BACKBONE HBOND TOGGLE");
      backboneHBondsToggle.addActionListener(this);
      controlPanel.add(backboneHBondsToggle);
      
      JToggleButton labelsToggle = new JToggleButton("Labels");
      labelsToggle.setActionCommand("LABELS TOGGLE");
      labelsToggle.addActionListener(this);
      controlPanel.add(labelsToggle);
      
      ButtonGroup pickingGroup = new ButtonGroup();
      
      JToggleButton pickDistanceToggle = new JToggleButton("Pick Distance");
      pickDistanceToggle.setActionCommand("PICK DISTANCE");
      pickDistanceToggle.addActionListener(this);
      pickingGroup.add(pickDistanceToggle);
      controlPanel.add(pickDistanceToggle);
      
      JToggleButton pickAngleToggle = new JToggleButton("Pick Angle");
      pickAngleToggle.setActionCommand("PICK ANGLE");
      pickAngleToggle.addActionListener(this);
      pickingGroup.add(pickAngleToggle);
      controlPanel.add(pickAngleToggle);
      
      JToggleButton pickTorsionToggle = new JToggleButton("Pick Torsion");
      pickTorsionToggle.setActionCommand("PICK TORSION");
      pickTorsionToggle.addActionListener(this);
      pickingGroup.add(pickTorsionToggle);
      controlPanel.add(pickTorsionToggle);
      
      this.add(controlPanel, BorderLayout.NORTH);
      
      JmolCanvas canvas = new JmolCanvas();
      this.viewer = canvas.viewer;
      this.add(canvas, BorderLayout.CENTER);
      
      // XXX a hack to let the MultipleAnalysisPanel avoid setting the listener
      if (resultTable != null) {
    	  this.resultTable = resultTable;
    	  this.resultTable.addListSelectionListener(this);
      }
      
      this.currentlyLoadedId = null;
      this.currentlySelectedMotif = null;
      this.currentSelection = null;
      
		// XXX TODO FIXME
      this.structureDirectory = new File("/Users/maclean/research/phd/structures");
    }
	
	public void setStructureDirectory(File structureDirectory) {
		this.structureDirectory = structureDirectory;
	}
	
	public File findFileForId(String id) throws FileNotFoundException {
		File file = new File(this.structureDirectory, id.toLowerCase() + ".pdb");
		
		if (file.exists()) {
			return file;
		} else {
			File[] fileList = this.structureDirectory.listFiles();
			for (File existingFile : fileList) {
				if (existingFile.getName().contains(id.toLowerCase())) {
					return existingFile;
				}
			}
			throw new FileNotFoundException("No file with id " + id + " found");
		}
		
	}
	
	/**
	 * Load a full structure into the Jmol view.
	 * 
	 * @param id The pdbid, resolved to a file path based on the currently set directory.
	 */
	public void loadStructure(String id) {
		// no point in loading the same structure twice...
		if (this.currentlyLoadedId != null && this.currentlyLoadedId.equals(id)) {
			return;
		}
		
		try {
			File file = findFileForId(id);
			Reader reader = new FileReader(file);
			
			this.viewer.evalString("zap");	// XXX don't know if we need this?
			
			this.viewer.openReader(id, file.toString(), reader);
			this.currentlyLoadedId = id;
		} catch (FileNotFoundException fnfe) {
			System.err.println(fnfe);
		}
	}
	
	/**
	 * Select a particular set of atoms in a whole structure.
	 * 
	 * @param motifString The residues/atoms to select.
	 */
	public void selectMotif(String motifString) {
		if (this.currentlySelectedMotif != null && this.currentlySelectedMotif.equals(motifString)) {
			return;
		}
		
		String chainLetter = motifString.substring(motifString.indexOf(".") + 1, motifString.indexOf(" "));
		String residueString = motifString.substring(motifString.indexOf(" ") + 1);
		String start = residueString.substring(3, residueString.indexOf("-"));
		String end = residueString.substring(residueString.indexOf("-") + 4);
		
		// this is a hack to get around not always having the correct chain letter. The results set a default
		// of chain "A" if there is no chain, but this messes things up...
		String selection = String.format("not water and ((%s-%s and :%s) or (%s-%s))", 
											start, end, chainLetter, start, end);
		
		String script = "wireframe off; spacefill off; select " + selection 
						+ "; zoom (selected) 0;" + this.displayScriptFragment;
		
		System.out.println(script);
		
		this.viewer.evalString(script);
		this.currentlySelectedMotif = motifString;
		this.currentSelection = selection;
	}
	
	public void clear() {
		this.viewer.evalString("zap");
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		String script;
		
		if (command.equals("SIDECHAIN TOGGLE")) {
			if (((JToggleButton) ae.getSource()).isSelected()) {
				script = "select selected and sidechain; " + this.displayScriptFragment
						+ " select " + this.currentSelection;
			} else {
				script = "select selected and sidechain; wireframe off; spacefill off; select " + this.currentSelection;
			}
		} else if (command.equals("BACKBONE HBOND TOGGLE")) {
			if (((JToggleButton) ae.getSource()).isSelected()) {
				// XXX : is there a way to check for already calculated hbonds?
				script = "hbonds calculate; hbonds on";		
			} else {
				script = "hbonds off";
			}
		} else if (command.equals("LABELS TOGGLE")) {
			if (((JToggleButton) ae.getSource()).isSelected()) {
				
				// XXX note hack : have to use "\n" after label command - is this a Jmol bug? 
				script = "select *.ca and selected; label %r\n color label black; select " + this.currentSelection;
			} else {
				script = "labels off";
			}
		} else if (command.equals("PICK DISTANCE")) {
			script = "set measurements angstroms; set pickingStyle measure on; set picking measure distance";
		} else if (command.equals("PICK ANGLE")) {
			script = "set measurements angstroms; set pickingStyle measure on; set picking measure angle";
		} else if (command.equals("PICK TORSION")) {
			script = "set measurements angstroms; set pickingStyle measure on; set picking measure torsion";
		} else {
			script = "";
		}
		
		System.out.println(script);
		this.viewer.evalString(script);


	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		int[] selectedRows = this.resultTable.getSelectedRows();
		if (selectedRows.length > 0) {
			int first = selectedRows[0];	// XXX
			try {
				String id = this.resultTable.getIdForRow(first);
				String motif = this.resultTable.getMotifForRow(first);
				this.loadStructure(id);
				this.selectMotif(motif);
			} catch (ArrayIndexOutOfBoundsException a) {
				System.out.println(a);
			}
		}
		
	}
	
	public class JmolCanvas extends JPanel {
		public JmolViewer viewer;
		private final Dimension currentSize = new Dimension(500, 500);
		private final Rectangle rectClip = new Rectangle();
		
		public JmolCanvas() {
			this.viewer = JmolViewer.allocateViewer(this, new SmarterJmolAdapter()); 
			this.viewer.setStringProperty("backgroundColor", "white");
			this.setPreferredSize(this.currentSize);
		}

		public void paintComponent(Graphics g) {
			g.getClipBounds(rectClip);
			this.viewer.renderScreenImage(g, currentSize, rectClip);
		}
	}
}
