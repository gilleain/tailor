package tailor.app;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * java.awt.Canvas subclass to draw Ramachandran-like scatterplots
 * 
 * @author maclean
 *
 */
public class RamachandranPlotCanvas extends Canvas {
	
	private ArrayList<PointList> pointLists;
    private ArrayList<Shape> bounds;
	
	private int xGridSeparation;
	private int yGridSeparation;
	private int xGridDisplaySeparation;
	private int yGridDisplaySeparation;
	
	private float scale;
	
	private int xBorder;
	private int yBorder;
	
	private int displayWidth;
	private int displayHeight;
	
	private Color currentColor;
	private Color[] colors = { Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE , Color.CYAN };
    
    private int keyX;
    private int keyY;
    
    private AffineTransform transform;
    
	public RamachandranPlotCanvas() {
        this.currentColor = this.colors[0];
        
		this.pointLists = new ArrayList<PointList>();
        this.pointLists.add(new PointList(this.currentColor));
        this.bounds = new ArrayList<Shape>();
		
		this.xBorder = 40;
		this.yBorder = 40;
		this.scale = 1.2f;
		
		this.xGridSeparation = 30;
		this.yGridSeparation = 30;
		
		this.xGridDisplaySeparation = (int) (this.xGridSeparation * this.scale);
		this.yGridDisplaySeparation = (int) (this.yGridSeparation * this.scale);
		
		this.displayWidth = (int) (360 * this.scale);
		this.displayHeight = (int) (360 * this.scale);
		
//		this.displayWidth = (int) (270 * this.scale);
//		this.displayHeight = (int) (270 * this.scale);
		
		
		int xOffset = this.xBorder * 2;
		int yOffset = this.yBorder * 2;
		this.setSize(this.displayWidth + xOffset, this.displayHeight + yOffset);
		
		this.keyX = this.displayWidth + this.xBorder + 10; 
		this.keyY = (this.displayHeight + this.yBorder) / 2;
        
        this.transform = AffineTransform.getTranslateInstance(this.xBorder, this.yBorder); 
        transform.concatenate(AffineTransform.getScaleInstance(this.scale, this.scale));
        AffineTransform xAxisReflection = new AffineTransform(1.0, 0.0, 0.0, -1.0, 0.0, 0.0);
        
        transform.concatenate(AffineTransform.getTranslateInstance(180.0, 180.0));
        transform.concatenate(xAxisReflection);
	}
    
    public int numberOfPointLists() {
        return this.pointLists.size();
    }
    
    public void addBoundShape(Shape shape) {
        this.bounds.add(this.transform.createTransformedShape(shape));
    }
    
    public void clearBounds() {
        this.bounds.clear();
        for (PointList pointList : this.pointLists) {
            pointList.showAll();
        }
    }
    
    public void clearPoints() {
        this.bounds.clear();
        this.pointLists.clear();
        this.currentColor = this.colors[0];
        this.pointLists.add(new PointList(this.currentColor));
    }
    
    public void selectOnlyCategory(Category category) {
        ArrayList<String> ids = category.getMemberIds();
        for (PointList pointList : this.pointLists) {
            pointList.showOnlyPointsWithId(ids);
        }
    }
	
	public void addPoint(int listIndex, String id, double x, double y) {
		int xint = this.transformXToCanvasSpace((int) x); 
		int yint = this.transformYToCanvasSpace((int) y);
        
        try {
            PointList points = this.pointLists.get(listIndex);
            points.add(new Point(id, xint, yint));
        } catch (IndexOutOfBoundsException i) {
            this.incrementColorState();
            PointList points = new PointList(this.currentColor);
            points.add(new Point(id, xint, yint));
            this.pointLists.add(points);
            
        }
	}
    
    private void incrementColorState() {
    	int currentColorIndex = -1;
    	for (int i = 0; i < this.colors.length; i++) {
    		if (this.currentColor == this.colors[i]) {
    			currentColorIndex = i;
    			break;
    		}
    	}
    	
    	if (currentColorIndex != -1) {
    		this.currentColor = this.colors[currentColorIndex + 1];
    	} else {
    		// TODO : throw an exception?
    	}
    }

    private int transformXToCanvasSpace(int x) {
        return ((int) ((x + 180) * this.scale)) + this.xBorder;
    }
    
    private int transformYToCanvasSpace(int y) {
        return (int) ((180.0 - y) * this.scale) + this.yBorder;
    }
	
	private void paintMajorXGridLines(Graphics g, int w, int h, int xOffset, int yOffset) {
		for (int i = xOffset; i <= w + xOffset; i += this.xGridDisplaySeparation) {
			g.drawLine(i, yOffset, i, h + yOffset);
		}
	}
	
	private void paintXAxis(Graphics g, int w, int h, int xOffset, int yOffset) {
		int w2 = ((int) w / 2) + xOffset;
		g.setColor(Color.red);
		g.drawLine(w2, yOffset, w2, h + yOffset);
		g.setColor(Color.black);
	}
	
	private void paintMajorYGridLines(Graphics g, int w, int h, int xOffset, int yOffset) {
		for (int j = yOffset; j <= h + yOffset; j += this.yGridDisplaySeparation) {
			g.drawLine(xOffset, j, w + xOffset, j);
		}
	}

	private void paintYAxis(Graphics g, int w, int h, int xOffset, int yOffset) {
		int h2 = (int) h / 2 + yOffset;
		g.setColor(Color.red);
		g.drawLine(xOffset, h2, w + xOffset, h2);
		g.setColor(Color.black);
	}

	private void paintPoints(Graphics g) {
        for (PointList pointList : this.pointLists) {
            pointList.paint(g);
        }
	}
    
    private void paintBounds(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2.0f));
        for (Shape shape : this.bounds) {
            g2.draw(shape);
        }
    }
	
	private void paintXAxisTics(Graphics g, int w, int h, int xOffset, int yOffset) {
		FontMetrics fontMetrics = g.getFontMetrics();
		
		int i = xOffset;
		int buffer = 3;
		for (int l = -180; l <= 180; l += this.xGridSeparation) {
			String label = String.valueOf(l);
			Rectangle2D stringBounds = fontMetrics.getStringBounds(label, g);
			double labelWidth = stringBounds.getWidth();
			double labelHeight = stringBounds.getHeight();
			int x = (int) (i - (labelWidth / 2.0));
			int y = (int) (h + yOffset + labelHeight + buffer);
            
            // FIXME TEMP
            if (l == -180 || l == -90 || l == 0 || l == 90 || l == 180) {
                g.drawString(label, x, y);
            }
			i += this.xGridDisplaySeparation;
		}

	}
	
	private void paintYAxisTics(Graphics g, int w, int h, int xOffset, int yOffset) {
		FontMetrics fontMetrics = g.getFontMetrics();
		
		int i = yOffset;
		int buffer = 3;
		for (int l = 180; l >= -180; l -= this.yGridSeparation) {
			String label = String.valueOf(l);
			Rectangle2D stringBounds = fontMetrics.getStringBounds(label, g);
			double labelWidth = stringBounds.getWidth();
			double labelHeight = stringBounds.getHeight();
			int x = (int) (xOffset - labelWidth - buffer);
			int y = (int) (i + (labelHeight / 2.0));
            
            // FIXME TEMP
            if (l == -180 || l == -90 || l == 0 || l == 90 || l == 180) {
                g.drawString(label, x, y);
            }
			i += this.yGridDisplaySeparation;
		}
	}
    
    private void paintAxisLabels(Graphics g, int w, int h, int xOffset, int yOffset) {
        g.drawString("\u03C8\u00B0", 10, yOffset + (h / 2));   
        g.drawString("\u03C6\u00B0", xOffset + (w / 2), h + yOffset + 30);
    }

    private void paintKey(Graphics g) {
        int currentY = this.keyY;
        for (int i = 0; i < this.numberOfPointLists(); i++) {
            g.setColor(this.colors[i]);
            g.drawString(String.valueOf(i + 1), this.keyX, currentY);
            currentY += 20;
        }
    }
	
    @Override
	public void paint(Graphics g) {
		int w = this.displayWidth;
		int h = this.displayHeight;
		
        Color oldColor = g.getColor();
        g.setColor(Color.WHITE);
        g.fillRect(this.xBorder, this.yBorder, this.displayWidth, this.displayHeight);
        g.setColor(oldColor);
        
        g.setColor(Color.GRAY);
        
		this.paintMajorXGridLines(g, w, h, this.xBorder, this.yBorder);
		this.paintXAxis(g, w, h, this.xBorder, this.yBorder);
		this.paintXAxisTics(g, w, h, this.xBorder, this.yBorder);
		
		this.paintMajorYGridLines(g, w, h, this.xBorder, this.yBorder);
		this.paintYAxis(g, w, h, this.xBorder, this.yBorder);
		this.paintYAxisTics(g, w, h, this.xBorder, this.yBorder);

        this.paintAxisLabels(g, w, h, this.xBorder, this.yBorder);
        this.paintKey(g);
        
		this.paintPoints(g);
        this.paintBounds(g);
        
        g.setColor(oldColor);
	}
    
    public Image grabImage() {
        Image image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        this.paint(g);
        
        return image;
    }
    
    private class PointList {
        
        private Color color;
        private ArrayList<Point> points;
        
        public PointList(Color color) {
            this.color = color;
            this.points = new ArrayList<Point>();
        }
        
        public void add(Point point) {
            this.points.add(point);
        }
        
        public void paint(Graphics g) {
            g.setColor(this.color);
            for (Point point : this.points) {
                if (point.isDisplayed()) {
                    point.paint(g);
                }
            }
        }
  
        public void showOnlyPointsWithId(ArrayList<String> ids) {
            for (Point point : this.points) {
                for (String id : ids) {
                    if (point.hasId(id)) {
                        if (point.isDisplayed()) {
                            continue;
                        } else {
                            point.toggle();
                        }
                        break;
                    } else {
                        if (point.isDisplayed()) {
                            point.toggle();
                        } else {
                            continue;
                        }
                    }
                }
            }
        }
        
        public void showOnlyPointsInBound(Bound bound) {
            for (Point point : this.points) {
                if (bound.contains(point)) {
                    if (point.isDisplayed()) {
                        continue;
                    } else {
                        point.toggle();
                    }
                } else {
                    if (point.isDisplayed()) {
                        point.toggle();
                    } else {
                        continue;
                    }
                }
            }
        }
        
        public void hideAll() {
            for (Point point : this.points) {
                if (point.isDisplayed()) {
                    point.toggle();
                } else {
                    continue;
                }
            }
        }
        
        public void showAll() {
            for (Point point : this.points) {
                if (point.isDisplayed()) {
                    continue;
                } else {
                    point.toggle();
                }
            }
        }
    }
	
	private class Point {
		
		private String id;
		private int canvasX;
		private int canvasY;
		private boolean displayed;
		
		public Point(String id, int x, int y) {
			this.id = id;
			this.canvasX = x;
			this.canvasY = y;
			this.displayed = true;
		}
        
        public void paint(Graphics g) {
            g.drawRect(this.canvasX, this.canvasY, 1, 1);
        }
		
		public void toggle() {
			this.displayed = !this.displayed;
		}
		
		public boolean isDisplayed() {
			return this.displayed;
		}
		
		public String getId() {
			return this.id;
		}
        
        public boolean hasId(String id) {
            return this.id.equals(id);
        }
        
        public int getCanvasX() {
            return this.canvasX;
        }
        
        public int getCanvasY() {
            return this.canvasY;
        }
	}

    private class Bound {
        
        private String id;
        private int xMin;
        private int yMin;
        private int xMax;
        private int yMax;
        
        /**
         * @param id
         * @param xMin
         * @param yMin
         * @param xMax
         * @param yMax
         */
        public Bound(String id, int xMin, int yMin, int xMax, int yMax) {
            this.id = id;
            this.xMin = xMin;
            this.yMin = yMin;
            this.xMax = xMax;
            this.yMax = yMax;
        }

        public void paint(Graphics g) {
            Color oldColor = g.getColor();
            g.setColor(Color.BLACK);
            g.drawRect(this.xMin, this.yMin, this.xMax - this.xMin, this.yMax - this.yMin);
            g.setColor(oldColor);
        }
        
        public String getId() {
            return this.id;
        }
        
        public boolean contains(Point point) {
            int x = point.getCanvasX();
            int y = point.getCanvasY();
//            System.err.println(this.xMin + " < " + x + " < " + this.xMax);
//            System.err.println(this.yMin + " < " + x + " < " + this.yMax);
            return this.xMin < x && this.xMax > x && this.yMin < y && this.yMax > y;
        }
    }
}
