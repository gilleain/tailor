package tailor.app.filter;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;


/**
 * @author maclean
 *
 */
public class Bound implements Filter {
    
    public static BoundShape shapeType;

    private int xColumnIndex;
    
    private int yColumnIndex;
    
    private Shape[] shapes;
    
    public Bound(int xColumnIndex, int yColumnIndex, double minX, double maxX, double minY, double maxY, BoundShape shapeType) {
        this.xColumnIndex = xColumnIndex;
        this.yColumnIndex = yColumnIndex;
        
        if (minX > maxX) {
            
            // PATHOLOGICAL CORNER CASE!
            if (minY > maxY) {
                // TODO : handle this just to be complete...
            } else {
                // SPLITPHI
                this.shapes = new Shape[2];
                if (shapeType == BoundShape.RECTANGULAR) {
                    this.shapes[0] = new Rectangle2D.Double(minX, minY, 180 - minX, maxY - minY);
                    this.shapes[1] = new Rectangle2D.Double(-180, minY, maxX + 180, maxY - minY);
                } else if (shapeType == BoundShape.OVAL) {
                    double topWidth = 180 - minX;
                    double botWidth = maxX + 180;
                    double topX = minX + (topWidth / 2);
                    double botX = -180 - (botWidth / 2);
                    this.shapes[0] = new Arc2D.Double(topX, minY, topWidth, maxY - minY, 90, 180, Arc2D.CHORD);
                    this.shapes[1] = new Arc2D.Double(botX, minY, botWidth, maxY - minY, 90, -180, Arc2D.CHORD);
                }
            }
            
        } else if (minY > maxY) {
            // SPLITPSI
            this.shapes = new Shape[2];
            if (shapeType == BoundShape.RECTANGULAR) {
                this.shapes[0] = new Rectangle2D.Double(minX, minY, maxX - minX, 180 - minY);
                this.shapes[1] = new Rectangle2D.Double(minX, -180, maxX - minX, maxY + 180);
            } else if (shapeType == BoundShape.OVAL) {
                double topHeight = 180 - minY;
                double botHeight = maxY + 180;
                double topY = minY + (topHeight / 2);
                double botY = -180 - (botHeight / 2);
                this.shapes[0] = new Arc2D.Double(minX, topY, maxX - minX, topHeight, 0, 180, Arc2D.CHORD);
                this.shapes[1] = new Arc2D.Double(minX, botY, maxX - minX, botHeight, 0, -180, Arc2D.CHORD);
            }
        } else {
            // unsplit
            this.shapes = new Shape[1];

            if (shapeType == BoundShape.RECTANGULAR) {
                this.shapes[0] = new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
            } else if (shapeType == BoundShape.OVAL) {
                this.shapes[0] = new Ellipse2D.Double(minX, minY, maxX - minX, maxY - minY);
            }
        }
        
        Bound.shapeType = shapeType;
    }

  
    public boolean accept(double[] values) {
        double xValue = values[this.xColumnIndex];
        double yValue = values[this.yColumnIndex];
        for (Shape shape : this.shapes) {
            if (shape.contains(xValue, yValue)) {
                return true;
            }
        }
        return false;
    }
    
    public void rotateAboutCenter(double theta) {
        double centerX = this.shapes[0].getBounds().getCenterX();
        double centerY = this.shapes[0].getBounds().getCenterY();
        AffineTransform rotation = AffineTransform.getRotateInstance(Math.toRadians(theta), centerX, centerY);
        this.shapes[0] = rotation.createTransformedShape(this.shapes[0]);
    }

    public Shape[] getShapes() {
        return this.shapes;
    }

    public double getMinX() {
//      TODO : FIX
        return this.shapes[0].getBounds().getMinX();
    }

    public double getMaxX() {
//      TODO : FIX
        return this.shapes[0].getBounds().getMaxX();
    }

    public double getMinY() {
//      TODO : FIX
        return this.shapes[0].getBounds().getMinY();
    }

    public double getMaxY() {
//      TODO : FIX
        return this.shapes[0].getBounds().getMaxY();
    }

    public String toString() {
    	return String.format("([%.0f : %.0f], [%.0f : %.0f])", this.getMinX(), this.getMaxX(), this.getMinY(), this.getMaxY());
    }

}