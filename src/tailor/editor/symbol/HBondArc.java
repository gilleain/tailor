package tailor.editor.symbol;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Arc2D;


public class HBondArc extends Symbol {
	
	private PeptideHalfSquare left;
	private PeptideHalfSquare right;

	public HBondArc(PeptideHalfSquare left, PeptideHalfSquare right, Symbol.Stroke strokeType) {
		super("");
		this.left = left;
		this.right = right;
		this.setStroke(strokeType);
	}
	
	public boolean contains(Symbol symbol) {
		return this.right == symbol || this.left == symbol;
	}
	
	public void reshape(int centerX, int centerY, int size) {
		// the penalty of extending a common Symbol base class!
		this.recreateShape();
	}
	
	public Shape createShape() {
		Point leftTopCenter = this.left.getTopCenter();
		Point rightTopCenter = this.right.getTopCenter();
		int x = leftTopCenter.x;
		int w = rightTopCenter.x - x;
		int h = w / 3;
		int y = leftTopCenter.y - (h / 2);
		
		return new Arc2D.Float(x, y, w, h, 0, 180, Arc2D.OPEN);
	}
	
	public Color getColor() {
		int leftIndex = this.left.getResidueIndex();
		int rightIndex = this.right.getResidueIndex();
		
		int d;
		if (left.getLabel().equals("N")) {
			d = leftIndex - rightIndex;		// negative for N->O
		} else {
			d = rightIndex - leftIndex;		// positive for O->N (more frequent)
		}
		
		switch (d) {
			case 2 : return Color.DARK_GRAY;
			case 3 : return Color.MAGENTA;
			case 4 : return Color.RED;
			case 5 : return Color.ORANGE;
			
			case -2: return Color.DARK_GRAY;
			case -3: return Color.BLUE;
			case -4: return Color.GREEN;
			case -5: return Color.WHITE;
			default : return Color.YELLOW;
		}
	}

}
