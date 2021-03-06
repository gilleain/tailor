package tailor.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.DescriptionFactory;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;
import tailor.editor.symbol.Symbol;

public class ResidueDiagramCanvas extends JPanel implements MouseListener, Scrollable {
	
	public static final int MIN_WIDTH = 920;
	public static final int MIN_HEIGHT = 300;
	
	private int numberOfResiduesInView;
	
	private int canvasWidth;
	private int canvasHeight;
	
	private int viewportWidth;
	
	private ResidueDiagram diagram;
	private List<SymbolSelectionListener> selectionListeners;
	
	/**
	 * @param numberOfResidues The total residues to display.
	 * @param numberOfResiduesInView 
	 * 		The number of residues to show in the scrollable viewport.
	 */
	public ResidueDiagramCanvas(ChainDescription chainDescription, int numberOfResiduesInView) {
		this.diagram = new ResidueDiagram(chainDescription);
		this.numberOfResiduesInView = numberOfResiduesInView;
		
		this.calculateDimensions(numberOfResiduesInView);
		this.setBackground(Color.LIGHT_GRAY);
		
		this.addMouseListener(this);
		this.selectionListeners = new ArrayList<>();
	}
	
	public void toggleLabels() {
		this.diagram.toggleLabels();
		this.repaint();
	}
	
	public void createFromDescription(ProteinDescription description) {
		this.diagram = new ResidueDiagram(description);
		this.calculateDimensions(this.numberOfResiduesInView);
	}
	
	public AtomDescription getAtomDescriptionFromSymbol(Symbol symbol) {
		return this.diagram.getAtomDescriptionFromSymbol(symbol);
	}
	
	public boolean canScaleDiagramUp() {
		return this.diagram.canScaleUp();
	}
	
	public boolean canScaleDiagramDown() {
		return this.diagram.canScaleDown();
	}
	
	public void scaleUp() {
		if (this.diagram.canScaleUp()) {
			this.diagram.scaleUp();
//			this.center();
			this.repaint();
		}
	}
	
	public void scaleDown() {
		if (this.diagram.canScaleDown()) {
			this.diagram.scaleDown();
//			this.center();
			this.repaint();
		}
	}
	
	public void scaleDiagram(float scaleFactor) {
		this.diagram.scale(scaleFactor);
//		this.center();
		this.repaint();
	}
	
	public void addResidueToEnd(GroupDescription group) {
		this.diagram.addResidueToEnd(group);
//		this.center();
	}
	
	public void removeResidueFromEnd() {
		this.diagram.removeResidueFromEnd();
	}
	
	public void center() {
		this.diagram.relayout(this.canvasWidth / 2, this.canvasHeight / 2);
	}
 	
	public Map<Symbol, AtomDescription> createMap(DescriptionFactory factory) {
		Map<Symbol, AtomDescription> map = new HashMap<>();
		List<Symbol> symbols = this.diagram.getBackboneSymbols();
		
		ChainDescription chain = factory.getChainDescription("A");	// TODO : >1 chain?
		int symbolIndex = 0;
		for (int residueNumber = 0; residueNumber < chain.size(); residueNumber++) {
			GroupDescription group = chain.getGroupDescription(residueNumber);
			map.put(symbols.get(symbolIndex++), group.getAtomDescription("N"));
			map.put(symbols.get(symbolIndex++), group.getAtomDescription("CA"));
			map.put(symbols.get(symbolIndex++), group.getAtomDescription("O"));
		}
		
		return map;
	}
	
	public void addSymbolSelectionListener(SymbolSelectionListener l) {
		this.selectionListeners.add(l);
	}
	
	public void makeBond(Symbol startSymbol, Symbol endSymbol, Symbol.Stroke strokeType) {
		diagram.makeBond(startSymbol, endSymbol, strokeType);
		diagram.unselectAllBackboneSymbols();
		this.repaint();
	}
	
	public void makePhi(int residueNumber, String label, Symbol.Stroke strokeType) {
		Symbol firstO = diagram.getPeptideCarbonyl(residueNumber - 1);
		Symbol lastO = diagram.getPeptideCarbonyl(residueNumber);
		this.makeTorsion(label, firstO, lastO, residueNumber, 2, strokeType);
	}
	
	public void makePsi(int residueNumber, String label, Symbol.Stroke strokeType) {
		Symbol firstN = diagram.getPeptideAmide(residueNumber);
		Symbol lastN = diagram.getPeptideAmide(residueNumber + 1);
		this.makeTorsion(label, firstN, lastN, residueNumber, 3, strokeType);
	}
	
	private void makeTorsion(String label, Symbol start, Symbol end, 
			int residueNumber, int level, Symbol.Stroke strokeType) {
		this.diagram.makeTorsion(residueNumber, label, start, end, level, strokeType);
		this.repaint();
	}
	
	private void fireSymbolSelectionEvent(Symbol selected) {
		SymbolSelectionEvent e = new SymbolSelectionEvent(selected);
		for (SymbolSelectionListener l : this.selectionListeners) {
			l.symbolSelection(e);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		double x = (double) e.getX();
		double y = (double) e.getY();
		
		Symbol selected = this.diagram.getSymbolAt(x, y);
		if (selected != null) {
			this.fireSymbolSelectionEvent(selected);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// no action
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
        // no action		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	      // no action
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	      // no action
	}
	
	public void calculateDimensions(int numberOfResiduesInView) {
		int dw = this.diagram.getWidth();
		int dh = this.diagram.getHeight();
		this.canvasWidth = Math.max(ResidueDiagramCanvas.MIN_WIDTH, dw);
		this.canvasHeight = Math.max(ResidueDiagramCanvas.MIN_HEIGHT, dh);
		
		int twoN = 2 * numberOfResiduesInView;
		this.viewportWidth = twoN * (this.diagram.getShapeSizePlusGapSize());
	}
	
	public ResidueDiagram getDiagram() {
		return this.diagram;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.diagram.paint(g);
	}
	
	/**
	 * Scrolls the panel to show a particular residue on the far left.
	 * 
	 * @param index The residue index to show on the far left.  
	 */
	public void scrollToResidue(int index) {
		int startX = this.diagram.getBackboneSymbolCenterX(index);
		this.scrollRectToVisible(new Rectangle(startX, 0, this.viewportWidth, this.canvasHeight));
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(this.canvasWidth, this.canvasHeight);
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(this.viewportWidth, this.canvasHeight);
	}
	
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		// move by an odd number to alternate between square and circle.
		// move by an even number to click along the circles.
		return 3 * (this.diagram.getShapeSizePlusGapSize());
	}
	
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return this.diagram.getShapeSizePlusGapSize();
	}
	
	@Override
	public boolean getScrollableTracksViewportHeight() {
		return true;
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;		
	}

}
