package tailor.editor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import tailor.condition.Condition;
import tailor.condition.HBondCondition;
import tailor.condition.TorsionBoundCondition;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.DescriptionFactory;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;
import tailor.editor.symbol.HBondArc;
import tailor.editor.symbol.PeptideHalfSquare;
import tailor.editor.symbol.ResidueCircle;
import tailor.editor.symbol.Symbol;
import tailor.editor.symbol.TorsionBox;
import tailor.measure.TorsionMeasure;

public class ResidueDiagram {
	
	public static int MAX_SHAPE_SIZE = 60;
	public static int MAX_GAP_SIZE = 15;
	public static int MAX_TORSION_BOX_HEIGHT = 25;
	public static int MAX_TORSION_BOX_GAP = 5;
	public static float DEFAULT_SCALE_STEP = 0.1f;
	
	private float currentScaleFactor = 1.0f;
	private float scaleStep = ResidueDiagram.DEFAULT_SCALE_STEP;
	
	private int shapeSize;
	private int gapSize;
	private int torsionBoxHeight;
	private int torsionBoxGap;
	
	private int width;
	private int height;
	private int axis;
	
	private int numberOfResidues;
	private String name;
	
	private ArrayList<Symbol> backboneSymbols;
	private ArrayList<Symbol> torsionSymbols;
	private ArrayList<Symbol> hBondSymbols;
	
	private HashMap<Symbol, Object> symbolToObjectMap;
	private DescriptionFactory factory;
	private boolean shouldDrawLabels;
	
	public ResidueDiagram() {
		this.backboneSymbols = new ArrayList<Symbol>();
		this.torsionSymbols = new ArrayList<Symbol>();
		this.hBondSymbols = new ArrayList<Symbol>();
		
		this.shapeSize = ResidueDiagram.MAX_SHAPE_SIZE;
		this.gapSize = ResidueDiagram.MAX_GAP_SIZE;
		this.torsionBoxHeight = ResidueDiagram.MAX_TORSION_BOX_HEIGHT;
		this.torsionBoxGap = ResidueDiagram.MAX_TORSION_BOX_GAP;
		this.symbolToObjectMap = new HashMap<Symbol, Object>();
		this.factory = new DescriptionFactory();
		this.shouldDrawLabels = true;
	}
	
	public ResidueDiagram(int numberOfResidues) {
		this();
		this.numberOfResidues = numberOfResidues;
		this.name = "New Motif";
		this.createBackbone(numberOfResidues);
		this.factory.addChainToProtein("A");
		this.factory.addMultipleResiduesToChain("A", numberOfResidues);
		ChainDescription chain = factory.getChainDescription("A");	// TODO : >1 chain?
		this.fillMap(chain);
	}
	
	public ResidueDiagram(ProteinDescription description) {
		this();
		this.createFromDescription(description);
	}
	
	public void toggleLabels() {
		this.shouldDrawLabels = !this.shouldDrawLabels;
		this.setDrawLabels(this.shouldDrawLabels);
	}
	
	public void setDrawLabels(boolean drawLabel) {
		for (Symbol s : this.backboneSymbols) {
			s.setDrawLabel(drawLabel);
		}
		
		for (Symbol s : this.hBondSymbols) {
			s.setDrawLabel(drawLabel);
		}
		
		for (Symbol s : this.torsionSymbols) {
			s.setDrawLabel(drawLabel);
		}
	}
	
	public void createFromDescription(ProteinDescription description) {
		this.name = description.getName();
		this.factory.setDescription(description);
		
		ChainDescription chain = description.getChainDescription("A");	// TODO >1 chain
		this.numberOfResidues = chain.size();
		this.createBackbone(this.numberOfResidues);
		this.fillMap(chain);
		for (Condition condition : chain.getConditions()) {
			if (condition instanceof TorsionBoundCondition) {
				TorsionBoundCondition t = (TorsionBoundCondition) condition; 
				this.makeTorsion(t);
			} else if (condition instanceof HBondCondition) {
				HBondCondition h = (HBondCondition) condition;
				this.makeBond(h);
			}
		}
	}
	
	public void addHBondConditionToChain(HBondCondition hBondCondition, 
			int donorNumber, int acceptorNumber, String chainName) {
		this.factory.addHBondConditionToChain(hBondCondition, donorNumber, 
				acceptorNumber, chainName);
	}
	
	public void addPhiConditionToChain(TorsionBoundCondition torsionCondition, 
			int residueNumber, String chainName) {
		this.factory.addPhiConditionToChain(torsionCondition, residueNumber, chainName);
	}
	
	public void addPsiConditionToChain(TorsionBoundCondition torsionCondition, 
			int residueNumber, String chainName) {
		this.factory.addPsiConditionToChain(torsionCondition, residueNumber, chainName);
	}
	
	public void fillPhiMeasure(TorsionMeasure torsionMeasure, int residueNumber, 
			String chainName) {
		this.factory.fillPhiMeasure(torsionMeasure, residueNumber, chainName);
	}
	
	public void fillPsiMeasure(TorsionMeasure torsionMeasure, int residueNumber, 
			String chainName) {
		this.factory.fillPsiMeasure(torsionMeasure, residueNumber, chainName);
	}
	
	public boolean canHydrogenBond(AtomDescription a, AtomDescription b) {
		return this.factory.canHydrogenBond(a, b);
	}
	
	public void makeTorsion(TorsionBoundCondition t) {
		System.err.println("making torsion");
		
		Description firstDesc = t.getDescriptionA();
		Description lastDesc = t.getDescriptionD();
		
		// FIXME ugly hack to map C->O for phi  
		AtomDescription a = (AtomDescription) firstDesc.getPathEnd();
		if (a.getName() == "C") {
			a.setName("O");
		}
		AtomDescription d = (AtomDescription) lastDesc.getPathEnd();
		if (d.getName() == "C") {
			d.setName("O");
		}
		
		Symbol first = this.reverseLookup(firstDesc);
		Symbol last = this.reverseLookup(lastDesc);
		
		if (first == null || last == null) {
			return;							// FIXME
		}
		
		this.makeTorsion(-1, t.makeTorsionLabel(), first, last, 
				this.getTrackFromString(t.getLetterSymbol()), Symbol.Stroke.DASHED);
	}
	
	public int getTrackFromString(String s) {
		if (s.contains("\u03C6")) {
			return 2;
		} else if (s.contains("\u03C8")) {
			return 3;
		} // TODO : omega!
		else {
			return 4;	//??
		}
	}
	
	public void makeBond(HBondCondition h) {
		Symbol donor = this.reverseLookup(h.getDonorAtomDescription());
		Symbol acceptor = this.reverseLookup(h.getAcceptorAtomDescription());
		if (donor.getResidueIndex() < acceptor.getResidueIndex()) {
			this.makeBond(donor, acceptor, Symbol.Stroke.DASHED);
		} else {
			this.makeBond(acceptor, donor, Symbol.Stroke.DASHED);
		}
	}
	
	private Symbol reverseLookup(Description path) {
		AtomDescription atom = this.factory.lookup(path);
		System.err.print("lookup found : " + path.toPathString() + " -> " + atom);
		for (Symbol s : this.symbolToObjectMap.keySet()) {
			Object p = this.symbolToObjectMap.get(s);
			if (p.equals(atom)) {
				System.err.println(" mapped to " + s);
				return s;
			}
		}
		System.err.println(" no mapping!");
		return null;	// TODO : throw exception?
	}
	
	private void fillMap(ChainDescription chain) {
		int symbolIndex = 0;
		for (int residueNumber = 0; residueNumber < chain.size(); residueNumber++) {
			GroupDescription group = chain.getGroupDescription(residueNumber);
			System.out.println("got group " + group + " residueNumber " + residueNumber);
			Symbol nSymbol = this.backboneSymbols.get(symbolIndex++);
			Symbol caSymbol = this.backboneSymbols.get(symbolIndex++);
			Symbol oSymbol = this.backboneSymbols.get(symbolIndex++);
			this.symbolToObjectMap.put(nSymbol, group.getAtomDescription("N"));
			this.symbolToObjectMap.put(caSymbol, group.getAtomDescription("CA"));
			this.symbolToObjectMap.put(oSymbol, group.getAtomDescription("O"));
		}
	}
	
	public AtomDescription getAtomDescriptionFromSymbol(Symbol symbol) {
		return (AtomDescription) this.symbolToObjectMap.get(symbol);
	}
	
	/**
	 * Ask the diagram if its currentScaleFactor at max.
	 * 
	 * @return false if the currentScaleFactor is at max.
	 */
	public boolean canScaleUp() {
		if (this.currentScaleFactor == 1.0f) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Ask the diagram if its currentScaleFactor is at min.
	 * 
	 * @return false if the currentScaleFactor is at min.
	 */
	public boolean canScaleDown() {
		if (this.currentScaleFactor == this.scaleStep) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Scale the diagram up by a factor of <code>scaleStep</code>, without checking.
	 * Use <code>canScaleUp()</code> to check if it's okay to call this method. 
	 */
	public void scaleUp() {
		this.scale(this.currentScaleFactor + this.scaleStep);
		this.relayout();
	}
	
	/**
	 * Scale the diagram down by a factor of <code>scaleStep</code>, without checking.
	 * Use <code>canScaleDown()</code> to check if it's okay to call this method.
	 */
	public void scaleDown() {
		this.scale(this.currentScaleFactor - this.scaleStep);
		this.relayout();
	}
	
	public void scale(float scaleFactor) {
		this.currentScaleFactor = scaleFactor;
		System.out.println("scaleFactor=" + scaleFactor);
		this.shapeSize = (int) (ResidueDiagram.MAX_SHAPE_SIZE * scaleFactor);
		this.gapSize = (int) (ResidueDiagram.MAX_GAP_SIZE * scaleFactor);
		this.torsionBoxHeight = (int) (ResidueDiagram.MAX_TORSION_BOX_HEIGHT * scaleFactor);
		this.torsionBoxGap = Math.max(
				(int) (ResidueDiagram.MAX_TORSION_BOX_GAP * scaleFactor), 3);
	}
	
	
	/**
	 * Scale and center the ResidueDiagram so that it fits into this width and height. 
	 * 
	 * @param fitWidth the width to fit.
	 * @param fitHeight the height to fit.
	 */
	public void fitToSize(int fitWidth, int fitHeight) {
		this.calculateDimensions();
		
		// only scale if needed
		System.out.println("comparing " + this.calculateMaxWidth() + " and " + fitWidth);
		if (fitWidth < this.calculateMaxWidth()) {
			float scaleFactor = ((float) fitWidth) / ((float) this.width);
			this.scale(scaleFactor);
			this.calculateDimensions();
		}
		this.relayout(fitWidth / 2, fitHeight / 2);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		g.drawString(this.name, 10, 20);
		
		for (Symbol s : this.backboneSymbols) {
			s.draw(g2);
		}
		
		for (Symbol s : this.torsionSymbols) {
			s.draw(g2);
		}
		
		for (Symbol s : this.hBondSymbols) {
			s.draw(g2);
		}
	}
	
	public Image getImage() {
		Image image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        this.paint(g);
        
        return image;
	}
	
	/**
	 * Make symbols representing the backbone for <code>numberOfResidues</code> residues.
	 * 
	 * @param numberOfResidues the number of residues to create.
	 */
	public void createBackbone(int numberOfResidues) {
		this.calculateDimensions();
		
		this.axis = (1 * this.height) / 2;
		int s2 = this.shapeSize / 2;
		int s4 = s2 / 2;
		int centerX = this.gapSize + s4;
		int centerY = this.axis;
		
		Symbol n = new PeptideHalfSquare("N", 0, centerX, centerY, this.shapeSize);
		this.backboneSymbols.add(n);
		for (int i = 0; i < numberOfResidues; i++) {
			centerX += s4 + this.gapSize + s2;
			Symbol ca = new ResidueCircle(centerX, centerY, this.shapeSize, i, i + 1);
			this.backboneSymbols.add(ca);
			
			centerX += s2 + this.gapSize + s4;
			Symbol o = new PeptideHalfSquare("O", i, centerX, centerY, this.shapeSize);
			this.backboneSymbols.add(o);
			
			centerX += s2;
			if (i < numberOfResidues - 1) {
				n = new PeptideHalfSquare("N", i + 1, centerX, centerY, this.shapeSize);
				this.backboneSymbols.add(n);
			}
		}
	}
	
	
	/**
	 * Call this after changing scale, axis, or number of residues. 
	 * It does not create new symbols, but alters their size and position in-situ.
	 */
	public void relayout() {
		this.calculateDimensions();
		
		// if no centerX/centerY provided, assume that the diagram just fits the canvas
		int cx = this.width / 2;
		int cy = (1 * this.height) / 2;
		this.relayout(cx, cy);
	}
	
	public void calculateDimensions() {
		this.width = this.calculateWidth(this.shapeSize, this.gapSize);
		this.height = 300;
		System.out.println("Dimensions = " + this.width + ", " + this.height);
	}
	
	public int calculateWidth(int shapeSize, int gapSize) {
		int numberOfGaps = 2 * this.numberOfResidues;
		int numberOfShapes = numberOfGaps;
		
		return (numberOfShapes * shapeSize) + ((numberOfGaps + 2) * gapSize);
	}
	
	public int calculateMaxWidth() {
		return this.calculateWidth(
				ResidueDiagram.MAX_SHAPE_SIZE, ResidueDiagram.MAX_GAP_SIZE);
	}
	
	public void relayout(int cx, int cy) {
		this.axis = cy;
		int s2 = this.shapeSize / 2;
		int s4 = s2 / 2;
		
		// note that the relayout() call provides a 
		// cx = (this.width / 2), making startX = 0.
		int startX = cx - (this.width / 2);
		
		int centerX = startX + this.gapSize + s4;
		int centerY = this.axis;
		
		Symbol n = this.backboneSymbols.get(0);
		n.reshape(centerX, centerY, this.shapeSize);
		int symbolIndex = 1;
		for (int i = 0; i < this.numberOfResidues; i++) {
			centerX += s4 + this.gapSize + s2;
			
			Symbol ca = this.backboneSymbols.get(symbolIndex);
			ca.reshape(centerX, centerY, this.shapeSize);
			symbolIndex += 1;
			
			centerX += s2 + this.gapSize + s4;
			Symbol o = this.backboneSymbols.get(symbolIndex);
			o.reshape(centerX, centerY, this.shapeSize);
			symbolIndex += 1;
			
			centerX += s2;
			if (i < numberOfResidues - 1) {
				n = this.backboneSymbols.get(symbolIndex);
				n.reshape(centerX, centerY, this.shapeSize);
				symbolIndex += 1;
			}
		}
		
		for (Symbol hb : this.hBondSymbols) {
			hb.reshape(0, 0, 0);
		}
		
		for (Symbol t : this.torsionSymbols) {
			int track  = this.getTrackFromString(t.getLabel());
			int trackAxis = this.calculateTrackAxis(track);
			t.reshape(0, trackAxis, this.torsionBoxHeight);
		}
	}
	
	public int calculateTrackAxis(int track) {
		return this.axis + (track * (this.torsionBoxHeight + this.torsionBoxGap));
	}
	
	public int getNumberOfResidues() {
		return this.numberOfResidues;
	}
	
	public DescriptionFactory getFactory() {
		return this.factory;
	}
	
	public void addResidueToEnd() {
		this.numberOfResidues += 1;
		
		// create the symbols
		int rIdx = this.numberOfResidues - 1;
		Symbol nSymbol = new PeptideHalfSquare("N", rIdx, 0, 0, this.shapeSize);
		Symbol caSymbol = new ResidueCircle(0, 0, this.shapeSize, rIdx, rIdx + 1);
		Symbol oSymbol = new PeptideHalfSquare("O", rIdx, 0, 0, this.shapeSize);
		this.backboneSymbols.add(nSymbol);
		this.backboneSymbols.add(caSymbol);
		this.backboneSymbols.add(oSymbol);
		this.relayout();

		// now, map the new symbols to their AtomDescription counterparts
		this.factory.addResidueToChain("A", this.numberOfResidues - 1);
		ChainDescription chain = factory.getChainDescription("A");	// TODO : >1 chain?
		GroupDescription group = chain.getGroupDescription(this.numberOfResidues - 1);
		this.symbolToObjectMap.put(nSymbol, group.getAtomDescription("N"));
		this.symbolToObjectMap.put(caSymbol, group.getAtomDescription("CA"));
		this.symbolToObjectMap.put(oSymbol, group.getAtomDescription("O"));
	}
	
	public void removeResidueFromEnd() {
		this.numberOfResidues -= 1;
		int s = this.backboneSymbols.size();
		
		Symbol n = this.backboneSymbols.remove(s - 1);
		this.removeTorsionsWithSymbol(n);
		this.removeHBondsWithSymbol(n);
		this.symbolToObjectMap.remove(n);
		
		this.symbolToObjectMap.remove(this.backboneSymbols.remove(s - 2));
		
		Symbol o = this.backboneSymbols.remove(s - 3);
		this.removeTorsionsWithSymbol(o);
		this.removeHBondsWithSymbol(o);
		this.symbolToObjectMap.remove(o);
		
		this.relayout();
		
		ChainDescription chain = factory.getChainDescription("A");	// TODO : >1 chain?
		chain.removeLastGroupDescription();
	}
	
	private void removeTorsionsWithSymbol(Symbol s) {
		ArrayList<Symbol> torsionsToRemove = new ArrayList<Symbol>();
		for (Symbol tb : this.torsionSymbols) {
			if (((TorsionBox) tb).contains(s)) {	// FIXME : make a base class! 
				torsionsToRemove.add(tb);
			}
		}
		
		for (Symbol rtb : torsionsToRemove) {
			this.torsionSymbols.remove(rtb);
		}
	}
	
	private void removeHBondsWithSymbol(Symbol s) {
		ArrayList<Symbol> hBondsToRemove = new ArrayList<Symbol>();
		for (Symbol hb : this.hBondSymbols) {
			if (((HBondArc) hb).contains(s)) {		// FIXME : make a base class!
				hBondsToRemove.add(hb);
			}
		}
		for (Symbol rhb : hBondsToRemove) {
			this.hBondSymbols.remove(rhb);
		}
	}
	
	public int getBackboneSymbolCenterX(int index) {
		return this.backboneSymbols.get(index).getCenter().x;
	}
	
	public int getShapeSizePlusGapSize() {
		return this.shapeSize + this.gapSize;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<Symbol> getBackboneSymbols() {
		return this.backboneSymbols;
	}
	
	public Symbol getPeptideCarbonyl(int residueIndex) {
		for (Symbol s : this.backboneSymbols) {
			if (s instanceof PeptideHalfSquare 
					&& s.getResidueIndex() == residueIndex
					&& s.getLabel().equals("O")) {
				return s;
			}
		}
		return null;
	}
	
	public Symbol getPeptideAmide(int residueIndex) {
		for (Symbol s : this.backboneSymbols) {
			if (s instanceof PeptideHalfSquare 
					&& s.getResidueIndex() == residueIndex
					&& s.getLabel().equals("N")) {
				return s;
			}
		}
		return null;
	}
	
	public Symbol getSelectedBackboneSymbol(double x, double y) {
		for (Symbol s : this.backboneSymbols) {
			if (s.contains(x, y)) {
				return s;
			}
		}
		return null;
	}
	
	public Symbol getSymbolAt(double x, double y) {
		for (Symbol s : this.backboneSymbols) {
			if (s.contains(x, y)) {
				return s;
			}
		}
		
		for (Symbol s : this.hBondSymbols) {
			if (s.contains(x, y)) {
				return s;
			}
		}
		
		for (Symbol s : this.torsionSymbols) {
			if (s.contains(x, y)) {
				return s;
			}
		}
		
		return null;
	}
	
	public ArrayList<Symbol> getSelectedBackboneSymbols() {
		ArrayList<Symbol> selected = new ArrayList<Symbol>();
		for (Symbol s : this.backboneSymbols) {
			if (s.isSelected()) {
				selected.add(s);
			}
		}
		return selected;
	}
	
	public void unselectAllBackboneSymbols() {
		for (Symbol s : this.backboneSymbols) {
			s.setSelected(false);
		}
	}
	
	public void makeBond(Symbol start, Symbol end, Symbol.Stroke strokeType) {
		this.hBondSymbols.add(
				new HBondArc((PeptideHalfSquare) start, (PeptideHalfSquare) end, strokeType));
	}
	
	public void makeTorsion(int residueNumber, String label, Symbol first, 
			Symbol last, int trackNumber, Symbol.Stroke strokeType) {
		int torsionAxis = this.axis 
			 + (this.torsionBoxHeight * trackNumber)
			 + (this.torsionBoxGap * trackNumber);	
		this.torsionSymbols.add(
				new TorsionBox(residueNumber, label, 
								first, last, this.torsionBoxHeight, torsionAxis, strokeType));
	}
}
