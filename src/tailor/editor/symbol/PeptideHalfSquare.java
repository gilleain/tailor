package tailor.editor.symbol;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;


public class PeptideHalfSquare extends Symbol { 

	public PeptideHalfSquare(String label, int residueIndex, int x, int y, int diameter) {
		super(residueIndex, x, y, diameter, label);
		this.setDrawLabel(true);
	}
	
	public void reshape(int centerX, int centerY, int size) {
		this.setCenter(centerX, centerY);
		this.setDiameter(size);
		this.recreateShape();
	}

	public Shape createShape() {
		Point c = this.getCenter();
		int d = this.getDiameter();
		int r = d / 2;
		return new Rectangle2D.Float(c.x - (r / 2), c.y - r, r, d);
	}

	public Point getTopCenter() {
		Rectangle b = this.getShape().getBounds(); 
		return new Point(b.x + (b.width / 2), b.y);
	}
	
	public Color getColor() {
		return Color.BLACK;
	}

}
