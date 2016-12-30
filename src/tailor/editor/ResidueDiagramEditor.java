package tailor.editor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tailor.condition.Condition;
import tailor.condition.HBondCondition;
import tailor.condition.TorsionBoundCondition;
import tailor.datasource.xml.XmlDescriptionReader;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.ProteinDescription;
import tailor.editor.symbol.Symbol;
import tailor.measure.HBondMeasure;
import tailor.measure.Measure;
import tailor.measure.TorsionMeasure;

public class ResidueDiagramEditor extends JPanel implements SymbolSelectionListener {
	
	private ResidueDiagramCanvas canvas;
	
	private DiagramPropertyPanel diagramPropertyPanel;

	private MiscToolbar miscToolBar;
	private MeasureToolbar measureToolBar;
	private ConditionToolbar conditionToolBar;
	
	private ConditionPropertySheetPanel conditionPropertySheetPanel;
	private ConditionListBox conditionListBox;
	
	private MeasurePropertySheetPanel measurePropertySheetPanel;
	private MeasureListBox measureListBox;
	
	private Symbol previouslySelectedSymbol;
	
	public enum State {
		MAKING_HBOND_CONDITION,
		MAKING_PHI_TORSION_CONDITION,
		MAKING_PSI_TORSION_CONDITION,
		MAKING_HBOND_MEASURE,
		MAKING_PHI_TORSION_MEASURE,
		MAKING_PSI_TORSION_MEASURE
	};
	
	private State state;
	
	public ResidueDiagramEditor() {
		int numberOfResidues = 6;
		int numberOfResiduesInView = 6;
		
		this.canvas = new ResidueDiagramCanvas(numberOfResidues, numberOfResiduesInView);
		this.canvas.addSymbolSelectionListener(this);
		
		this.previouslySelectedSymbol = null;
		
		JScrollPane scrollPane = new JScrollPane(this.canvas, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setWheelScrollingEnabled(true);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		this.miscToolBar = new MiscToolbar(this, buttonGroup);
		this.conditionToolBar = new ConditionToolbar(this, buttonGroup);
		this.measureToolBar = new MeasureToolbar(this, buttonGroup);
		
		this.conditionPropertySheetPanel = new ConditionPropertySheetPanel();
		this.conditionListBox = new ConditionListBox();
		
		this.measurePropertySheetPanel = new MeasurePropertySheetPanel();
		this.measureListBox = new MeasureListBox();
		
		this.diagramPropertyPanel = new DiagramPropertyPanel("Motif", numberOfResidues);
		
		JPanel canvasAndButtonPanel = new JPanel(new BorderLayout());
		canvasAndButtonPanel.add(scrollPane, BorderLayout.SOUTH);
		
		JPanel toolBarPanel = new JPanel();
		toolBarPanel.add(this.miscToolBar);
		toolBarPanel.add(this.conditionToolBar);
		toolBarPanel.add(this.measureToolBar);
		
		JPanel conditionPanel = new JPanel(new BorderLayout());
		conditionPanel.add(this.conditionPropertySheetPanel, BorderLayout.WEST);
		conditionPanel.add(this.conditionListBox, BorderLayout.EAST);
		
		JPanel measurePanel = new JPanel(new BorderLayout());
		measurePanel.add(this.measurePropertySheetPanel, BorderLayout.EAST);
		measurePanel.add(this.measureListBox, BorderLayout.WEST);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(conditionPanel);
		bottomPanel.add(measurePanel);
		
		this.setLayout(new BorderLayout());
		this.add(toolBarPanel, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);
		
		this.state = State.MAKING_PHI_TORSION_CONDITION;
		this.conditionPropertySheetPanel.initialiseTorsionSheet("phi");
	}
	
	public void toggleLabels() {
		this.canvas.toggleLabels();
	}
	
	public void scaleUp() {
		this.canvas.scaleUp();
		if (!this.canvas.canScaleDiagramUp()) {
			this.miscToolBar.setScaleUpButtonEnabled(false);
		}
		if (this.canvas.canScaleDiagramDown()) {
			this.miscToolBar.setScaleDownButtonEnabled(true);
		}
	}
	
	public void scaleDown() {
		this.canvas.scaleDown();
		if (!this.canvas.canScaleDiagramDown()) {
			this.miscToolBar.setScaleDownButtonEnabled(false);
		}
		if (this.canvas.canScaleDiagramUp()) {
			this.miscToolBar.setScaleUpButtonEnabled(true);
		}
	}
	
	public void setDescription(Description description) {
		ProteinDescription p = (ProteinDescription) description;
		this.canvas.createFromDescription(p);
		for (ChainDescription chain : p.getChainDescriptions()) {
			for (Condition condition : chain.getConditions()) {
				this.conditionListBox.addConditionToList(condition);
			}
		}
	}
	
	public ProteinDescription getDescription() {
		return this.canvas.getDiagram().getFactory().getProduct();
	}
	
	public ArrayList<Measure> getMeasures() {
		return this.measureListBox.getMeasures();
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public void symbolSelection(SymbolSelectionEvent ss) {
		Symbol s = ss.getSymbol();
		switch (this.state) {
			case MAKING_HBOND_CONDITION: 	   this.makeBondCondition(s); break;
			case MAKING_PHI_TORSION_CONDITION: this.makePhiCondition(s); break;
			case MAKING_PSI_TORSION_CONDITION: this.makePsiCondition(s); break;
			case MAKING_HBOND_MEASURE: 	   	   this.makeBondMeasure(s); break;
			case MAKING_PHI_TORSION_MEASURE:   this.makePhiMeasure(s); break;
			case MAKING_PSI_TORSION_MEASURE:   this.makePsiMeasure(s); break;
			default:						   break;
		}
	}
	
	public void addResidueToEnd() {
		
		// update the display and the model
		this.canvas.addResidueToEnd();
		this.diagramPropertyPanel.incrementNumberOfResidues();
		
		// repaint changes
		this.canvas.calculateDimensions(6);	//FIXME
		this.repaint();
		
		// nasty, but seemingly necessary
		((JComponent)this.canvas.getParent()).revalidate();
	}
	
	public void removeResidueFromEnd() {
		
		// update the display and the model
		this.canvas.removeResidueFromEnd();
		this.diagramPropertyPanel.decrementNumberOfResidues();
		
		// repaint changes
		this.canvas.calculateDimensions(6); //FIXME
		this.repaint();
		
		// nasty, but seemingly necessary
		((JComponent)this.canvas.getParent()).revalidate();
	}
	
	public void makeBondCondition(Symbol newlySelectedSymbol) {
		
		if (this.previouslySelectedSymbol == null) {
			newlySelectedSymbol.setSelected(true);
			this.previouslySelectedSymbol = newlySelectedSymbol;
			this.canvas.repaint();
		} else {
			
			AtomDescription selectedAtomDescription = 
				this.canvas.getAtomDescriptionFromSymbol(newlySelectedSymbol);
			AtomDescription previousAtomDescription = 
				this.canvas.getAtomDescriptionFromSymbol(this.previouslySelectedSymbol);
			
			// bail out if incompatible partners have been selected
			if (!this.canvas.canHydrogenBond(selectedAtomDescription, 
					previousAtomDescription)) {
				return;
			}
			
			int startNum = this.previouslySelectedSymbol.getResidueIndex();
			int endNum = newlySelectedSymbol.getResidueIndex();
			
			HBondCondition hBondCondition = 
				this.conditionPropertySheetPanel.getHBondCondition();

			String startAtomLabel = this.previouslySelectedSymbol.getLabel();
			if (startAtomLabel.equals("O")) {	// endAtomType must be "N"
				this.conditionPropertySheetPanel.
				setHBondSheetResidues(endNum + 1, startNum + 1);
				this.canvas.addHBondConditionToChain(hBondCondition, endNum, startNum, "A");
			} else {							// endAtomType must be "O"
				this.conditionPropertySheetPanel.
					setHBondSheetResidues(startNum + 1, endNum + 1);
				this.canvas.addHBondConditionToChain(hBondCondition, startNum, endNum, "A");
			}

			this.conditionListBox.addConditionToList(hBondCondition);
			this.diagramPropertyPanel.incrementNumberOfHBondConditions();

			this.canvas.makeBond(this.previouslySelectedSymbol, 
					newlySelectedSymbol, Symbol.Stroke.DASHED);
			this.previouslySelectedSymbol.setSelected(false);
			this.previouslySelectedSymbol = null;
		}
	}
	
	public void makeBondMeasure(Symbol newlySelectedSymbol) {
		if (this.previouslySelectedSymbol == null) {
			this.previouslySelectedSymbol = newlySelectedSymbol;
		} else {
			
			AtomDescription selectedAtomDescription = 
				this.canvas.getAtomDescriptionFromSymbol(newlySelectedSymbol);
			AtomDescription previousAtomDescription = 
				this.canvas.getAtomDescriptionFromSymbol(this.previouslySelectedSymbol);
			
			// bail out if incompatible partners have been selected
			if (!this.canvas.canHydrogenBond(selectedAtomDescription, 
					previousAtomDescription)) {
				return;
			}
			
			int startNum = this.previouslySelectedSymbol.getResidueIndex();
			int endNum = newlySelectedSymbol.getResidueIndex();
			
			HBondMeasure hBondMeasure = this.measurePropertySheetPanel.getHBondMeasure();

			String startAtomLabel = this.previouslySelectedSymbol.getLabel();
			if (startAtomLabel.equals("O")) {	// endAtomType must be "N"
				this.measurePropertySheetPanel.setHBondSheetResidues(endNum + 1, startNum + 1);
				
			} else {							// endAtomType must be "O"
				this.measurePropertySheetPanel.setHBondSheetResidues(startNum + 1, endNum + 1);
			}

			this.measureListBox.addMeasureToList(hBondMeasure);
			this.diagramPropertyPanel.incrementNumberOfHBondConditions();

			this.canvas.makeBond(this.previouslySelectedSymbol, 
					newlySelectedSymbol, Symbol.Stroke.DOTTED);
			this.previouslySelectedSymbol = null;
		}
	}
	
	public void makePhiCondition(Symbol newlySelectedSymbol) {
		int residueNumber = newlySelectedSymbol.getResidueIndex();
		this.conditionPropertySheetPanel.setTorsionSheetResidue("phi", residueNumber, residueNumber + 1);
		
		TorsionBoundCondition torsionCondition = this.conditionPropertySheetPanel.getTorsionCondition();
		this.canvas.addPhiConditionToChain(torsionCondition, residueNumber, "A");
		this.showTorsionCondition(torsionCondition);
		
		torsionCondition.setLetterSymbol("\u03C6");
		String label = torsionCondition.makeTorsionLabel(); 
		this.canvas.makePhi(residueNumber, label, Symbol.Stroke.DASHED);
	}
	
	public void makePhiMeasure(Symbol newlySelectedSymbol) {
		int residueNumber = newlySelectedSymbol.getResidueIndex();
		this.measurePropertySheetPanel.setTorsionSheetResidue("phi", residueNumber, residueNumber + 1);
		
		TorsionMeasure torsionMeasure = this.measurePropertySheetPanel.getTorsionMeasure();
		this.canvas.fillPhiMeasure(torsionMeasure, residueNumber, "A");
		this.showTorsionMeasure(torsionMeasure);
		
		this.canvas.makePhi(residueNumber, "\u03C6?", Symbol.Stroke.DOTTED);
	}
	
	public void makePsiCondition(Symbol newlySelectedSymbol) {
		int residueNumber = newlySelectedSymbol.getResidueIndex();
		this.conditionPropertySheetPanel.setTorsionSheetResidue("psi", residueNumber + 1, residueNumber + 2);
		
		TorsionBoundCondition torsionCondition = this.conditionPropertySheetPanel.getTorsionCondition();
		this.canvas.addPsiConditionToChain(torsionCondition, residueNumber, "A");
		this.showTorsionCondition(torsionCondition);
		
		torsionCondition.setLetterSymbol("\u03C8");
		String label = torsionCondition.makeTorsionLabel();
		this.canvas.makePsi(residueNumber, label, Symbol.Stroke.DASHED);
	}
	
	public void makePsiMeasure(Symbol newlySelectedSymbol) {
		int residueNumber = newlySelectedSymbol.getResidueIndex();
		this.measurePropertySheetPanel.setTorsionSheetResidue("psi", residueNumber + 1, residueNumber + 2);
		
		TorsionMeasure torsionMeasure = this.measurePropertySheetPanel.getTorsionMeasure();
		this.canvas.fillPsiMeasure(torsionMeasure, residueNumber, "A");
		this.showTorsionMeasure(torsionMeasure);
		
		this.canvas.makePsi(residueNumber, "\u03C8?", Symbol.Stroke.DOTTED);
	}
	
	private void showTorsionCondition(TorsionBoundCondition torsionCondition) {
		this.conditionListBox.addConditionToList(torsionCondition);
		this.diagramPropertyPanel.incrementNumberOfTorsionConditions();
	}
	
	private void showTorsionMeasure(TorsionMeasure torsionMeasure) {
		this.measureListBox.addMeasureToList(torsionMeasure);
		this.diagramPropertyPanel.incrementNumberOfTorsionConditions(); // TODO : FIXME
	}
	
	public void saveImage() {
		Container parent = this.getTopLevelAncestor();
		FileDialog fileDialog; 
		if (parent instanceof JFrame) {
			fileDialog = new FileDialog((JFrame) parent, "Save Image To File", FileDialog.SAVE);
		} else {
			fileDialog = new FileDialog((JDialog) parent, "Save Image To File", FileDialog.SAVE);
		}
        fileDialog.setVisible(true);
        String directory = fileDialog.getDirectory();
        String filename = fileDialog.getFile();
        if (directory != null && filename != null) {
            Image image = this.canvas.getDiagram().getImage();
            File file = new File(directory, filename);
            try {
                ImageIO.write((RenderedImage) image, "PNG", file);
            } catch (IOException ioe) {
                System.err.println(ioe);
            }
        }
	}
	
	public void startMakingPhiTorsionCondition() {
		this.setState(ResidueDiagramEditor.State.MAKING_PHI_TORSION_CONDITION);
		this.conditionPropertySheetPanel.showSheet(ConditionPropertySheetPanel.TORSION_TAG);
		this.conditionPropertySheetPanel.initialiseTorsionSheet("phi");
	}
	
	public void startMakingPsiTorsionCondition() {
		this.setState(ResidueDiagramEditor.State.MAKING_PSI_TORSION_CONDITION);
        this.conditionPropertySheetPanel.showSheet(ConditionPropertySheetPanel.TORSION_TAG);
        this.conditionPropertySheetPanel.initialiseTorsionSheet("psi");
	}
	
	public void startMakingHBondCondition() {
		this.setState(ResidueDiagramEditor.State.MAKING_HBOND_CONDITION);
    	this.conditionPropertySheetPanel.showSheet(ConditionPropertySheetPanel.H_BOND_TAG);
	}
	
	public void startMakingPhiTorsionMeasure() {
		this.setState(ResidueDiagramEditor.State.MAKING_PHI_TORSION_MEASURE);
        this.measurePropertySheetPanel.showSheet(ConditionPropertySheetPanel.TORSION_TAG);
        this.measurePropertySheetPanel.initialiseTorsionSheet("phi");
	}
	
	public void startMakingPsiTorsionMeasure() {
		this.setState(ResidueDiagramEditor.State.MAKING_PSI_TORSION_MEASURE);
		this.measurePropertySheetPanel.showSheet(ConditionPropertySheetPanel.TORSION_TAG);
		this.measurePropertySheetPanel.initialiseTorsionSheet("psi");
	}
	
	public void startMakingHBondMeasure() {
		this.setState(ResidueDiagramEditor.State.MAKING_HBOND_MEASURE);
		this.measurePropertySheetPanel.showSheet(ConditionPropertySheetPanel.H_BOND_TAG);
	}
	
	public void loadMotifFile(File file) {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        Description description = reader.readDescription(file);
        this.canvas.createFromDescription((ProteinDescription)description);
    }
	
	public void processCommandLineArguments(String[] args) {
        String fileTypeFlag = args[0];
        if (fileTypeFlag.equals("--xml-motif")) {
        	File file = new File(args[1]);
        	if (file.exists()) {
        		this.loadMotifFile(file);
        	}
        }
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Residue Diagram Editor");
		ResidueDiagramEditor editor = new ResidueDiagramEditor();
		if (args.length > 0) {
            editor.processCommandLineArguments(args);
        }
		frame.add(editor);
		frame.pack();
		frame.setResizable(false);
		frame.setLocation(200,200);
		frame.setVisible(true);
	}
	
}
