package tailor.editor.symbol;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;


public class TorsionBox extends Symbol {
	
	private Symbol first;
	private Symbol last;
	private int axis;
	
	public TorsionBox(int residueNumber, String simpleName, Symbol first, Symbol last,
			int size, int axis, Symbol.Stroke strokeType) {
		super(residueNumber, simpleName);
		this.first = first;
		this.last = last;
		this.axis = axis;
		this.setDrawLabel(true);
		this.setStroke(strokeType);
		this.setDiameter(size);
	}
	
	public boolean contains(Symbol symbol) {
		return this.last == symbol || this.first == symbol;
	}
	
	public void reshape(int centerX, int centerY, int size) {
		// the penalty of extending a common Symbol base class!
		this.axis = centerY;
		this.setDiameter(size);
		this.recreateShape();
	}
	
	public Shape createShape() {
		Point firstCenter = this.first.getCenter();
		Point lastCenter = this.last.getCenter();
		
		int width = lastCenter.x - firstCenter.x;
		int spacer = 5;
		int size = this.getDiameter();
		return new Rectangle2D.Float(firstCenter.x + spacer, axis, width - spacer, size);
	}

	public Color getColor() {
		return Color.BLACK;
	}
}

