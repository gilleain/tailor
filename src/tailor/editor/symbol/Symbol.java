package tailor.editor.symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;


public abstract class Symbol {
	
	private int residueIndex;
	private int x;
	private int y;
	private int diameter;
	
	private Shape shape;
	private String label;
	
	private boolean isSelected;
	private boolean drawLabel;
	
	private float sw = 2.0f;
    private int cap = BasicStroke.CAP_SQUARE;
    private int join = BasicStroke.JOIN_BEVEL;
    private float miterlimit = 10.0f;
    private float[] dash = { 20, 5 };
    private float[] dash_dot = { 20, 5, 2, 5 };
    private float[] dot = { 2, 5 };
    private float dash_phase = 0.0f;

    private BasicStroke solidStroke = new BasicStroke(sw);
    private BasicStroke dashStroke = new BasicStroke(sw, cap, join, miterlimit, dash, dash_phase);
    private BasicStroke dashDotStroke = new BasicStroke(sw, cap, join, miterlimit, dash_dot, dash_phase);
    private BasicStroke dotStroke = new BasicStroke(sw, cap, join, miterlimit, dot, dash_phase);
    private BasicStroke currentStroke;
    
    public enum Stroke {
    	SOLID,
    	DASHED,
    	DOTTED,
    	DOTDASHED,
    }
	
	public Symbol(String label) {
		this(-1, label);
	}
	
	public Symbol(int residueIndex, String label) {
		this.residueIndex = residueIndex;
		this.isSelected = false;
		this.drawLabel = true;
		this.label = label;
		this.shape = null;
		this.currentStroke = this.solidStroke;
	}
	
	public Symbol(int residueIndex, int x, int y, int diameter, String label) {
		this(residueIndex, label);
		this.x = x;
		this.y = y;
		this.diameter = diameter;
	}
	
	public void setStroke(Symbol.Stroke strokeType) {
		switch (strokeType) {
			case SOLID: 	this.setSolidStroke(); break;
			case DASHED: 	this.setDashedStroke(); break;
			case DOTTED:	this.setDotStroke(); break;
			case DOTDASHED:	this.setDotDashedStroke(); break;
		}
	}
	
	public void setSolidStroke() {
		this.currentStroke = this.solidStroke;
	}
	
	public void setDashedStroke() {
		this.currentStroke = this.dashStroke;
	}
	
	public void setDotStroke() {
		this.currentStroke = this.dotStroke;
	}
	
	public void setDotDashedStroke() {
		this.currentStroke = this.dashDotStroke;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public int getResidueIndex() {
		return this.residueIndex;
	}
	
	public Point getCenter() {
		return new Point(this.x, this.y);
	}
	
	public void setCenter(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getDiameter() {
		return this.diameter;
	}
	
	public void setDiameter(int diameter) {
		this.diameter = diameter;
	}
	
	public Shape getShape() {
		if (this.shape == null) {
			this.recreateShape();
		}
		return this.shape;
	}
	
	public void recreateShape() {
		this.shape = this.createShape();
	}
	
	public abstract Shape createShape();
	
	public abstract Color getColor();
	
	public abstract void reshape(int centerX, int centerY, int size);

	public boolean contains(double x, double y) {
		return this.shape.contains(x, y);
	}

	public void draw(Graphics2D g2) {
		if (!(this instanceof HBondArc)) { // FIXME : must be a better way...
			g2.setColor(Color.WHITE);
			g2.fill(this.getShape());
		}
		
		if (this.isSelected) {
			g2.setColor(Color.RED);
		} else {
			g2.setColor(this.getColor());
		}
		g2.setStroke(this.currentStroke);
		g2.draw(this.getShape());
		
		if (this.drawLabel) {
			Font f = g2.getFont();
			FontRenderContext frc = g2.getFontRenderContext();
			Rectangle2D stringBounds = f.getStringBounds(this.label, frc);
			double sWidth = stringBounds.getWidth();
			Rectangle shapeBounds = this.shape.getBounds(); 
			int labelX = shapeBounds.x + (shapeBounds.width / 2) - (int)(sWidth / 2);
			int labelY = shapeBounds.y + (shapeBounds.height / 2) + (int)(stringBounds.getHeight() / 2);
			g2.drawString(this.label, labelX, labelY);
		}
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public void setDrawLabel(boolean drawLabel) {
		this.drawLabel = drawLabel;
	}

}
