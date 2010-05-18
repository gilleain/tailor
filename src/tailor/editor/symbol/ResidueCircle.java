package tailor.editor.symbol;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;


public class ResidueCircle extends Symbol {
	
	public ResidueCircle(int x, int y, int diameter, int residueIndex, int residueNumber) {
		super(residueIndex, x, y, diameter, String.valueOf(residueNumber));
		this.setDrawLabel(true);
	}
	
	public void reshape(int centerX, int centerY, int size) {
		this.setCenter(centerX, centerY);
		this.setDiameter(size);
		this.recreateShape();
	}
	
	public Shape createShape() {
		Point c = this.getCenter();
		int diameter = this.getDiameter();
		int r = diameter / 2;
		return new Ellipse2D.Float(c.x - r, c.y - r, diameter, diameter);
	}
	
	public Color getColor() {
		return Color.BLACK;
	}
	
}
