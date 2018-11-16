package tailor.app;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


/**
 * @author maclean
 *
 */
public class TorsionDial extends Canvas implements MouseListener, MouseMotionListener {
    
    private Ellipse2D circle;
    
    private Point2D center;
    
    private Tic[] tics;
    
    private Arc2D slice;
    
    private Ellipse2D startHandle;
    
    private Ellipse2D endHandle;
    
    private boolean startHandleSelected;
    
    private boolean endHandleSelected;
    
    private double handleD = 10;
    
    private double handleR = this.handleD / 2.0;
    
    private int xInset = 30;
    
    private int yInset = 30;
    
    public TorsionDial(int componentWidth, int componentHeight) {
        int minDimension = 0;
        int w = componentWidth - (2 * this.xInset);
        int h = componentHeight - (2 * this.yInset);
        
        if (w < h) {
            minDimension = w;
        } else if (h < w) {
            minDimension = h;
        } else {
            minDimension = w; //doesn't matter, since they are equal
        }
        this.circle = new Ellipse2D.Double(this.xInset, this.yInset, minDimension, minDimension);
        this.center = new Point2D.Double(this.circle.getCenterX(), this.circle.getCenterY());
        
        this.makeTics();
        
        this.slice = new Arc2D.Double(this.circle.getBounds2D(), 0, 40, Arc2D.PIE);
        
        this.startHandle = new Ellipse2D.Double();
        this.positionStartHandle();
        
        this.endHandle = new Ellipse2D.Double();
        this.positionEndHandle();
        
        this.startHandleSelected = false;
        this.endHandleSelected = false;
        
        this.setSize(w + (2 * this.xInset), h + (2 * this.yInset));
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    public void setMax(double value) {
        
    }
    
    public void setMin(double value) {
        
    }
    
    public double getMin() {
        return this.slice.getAngleStart();
    }
    
    public double getMax() {
        double max = this.slice.getAngleStart() + this.slice.getAngleExtent();
        if (max > 180) {
            return max - 360;
        } else {
            return max;
        }
    }
    
    private void makeTics() {
        this.tics = new Tic[4];
        
        // TODO : these are just guesses, could calculate
        int stringWidth = 30;
        int stringHeight = 15;
        
        Rectangle circleBounds = this.circle.getBounds();
        Point p1 = new Point((circleBounds.width / 2) + this.xInset, circleBounds.y);
        Point p2 = new Point(circleBounds.width + this.xInset, (circleBounds.height / 2) + this.yInset);
        Point p3 = new Point((circleBounds.width / 2) + this.xInset, circleBounds.height + this.yInset);
        Point p4 = new Point(circleBounds.x, (circleBounds.height / 2) + this.yInset);
        
        this.tics[0] = new Tic(new Line2D.Double(this.center, p1), "+90", p1.x - stringWidth / 2, p1.y - stringHeight / 2);
        this.tics[1] = new Tic(new Line2D.Double(this.center, p2), "0", p2.x, p2.y);
        this.tics[2] = new Tic(new Line2D.Double(this.center, p3), "-90", p3.x, p3.y + stringHeight);
        this.tics[3] = new Tic(new Line2D.Double(this.center, p4), "+/-180", p4.x - stringWidth, p4.y);
    }
    
    public void positionStartHandle() {
        Point2D startPoint = this.slice.getStartPoint();
        
        double startX = startPoint.getX() - this.handleR;
        double startY = startPoint.getY() - this.handleR;
        this.startHandle.setFrame(startX, startY, this.handleD, this.handleD);
    }
    
    public void positionEndHandle() {
        Point2D endPoint = this.slice.getEndPoint();
        
        double endX = endPoint.getX() - this.handleR;
        double endY = endPoint.getY() - this.handleR;
        this.endHandle.setFrame(endX, endY, this.handleD, this.handleD);
    }
    
    public void selectStartHandle() {
        this.startHandleSelected = true;
        this.endHandleSelected = false;
        this.repaint();
    }
    
    public void selectEndHandle() {
        this.startHandleSelected = false;
        this.endHandleSelected = true;
        this.repaint();
    }
    
    public void unselectHandles() {
        this.startHandleSelected = false;
        this.endHandleSelected = false;
        this.repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(this.slice);
        g2.setColor(Color.BLACK);
        g2.draw(this.slice);
        
        g2.setColor(Color.RED);
        if (this.startHandleSelected) {
            g2.fill(this.startHandle);
        } else {
            g2.draw(this.startHandle);
        }
        
        g2.setColor(Color.BLUE);
        if (this.endHandleSelected) {
            g2.fill(this.endHandle);
        } else {
            g2.draw(this.endHandle);
        }
        
        g2.setColor(Color.BLACK);
        g2.draw(this.circle);
        for (Tic tic : this.tics) {
            tic.paint(g2);
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent m) {}
    
    @Override
    public void mouseClicked(MouseEvent m) {}
    
    @Override
    public void mouseExited(MouseEvent m) {
        this.unselectHandles();
    }
    
    @Override
    public void mousePressed(MouseEvent m) {
        int x = m.getX();
        int y = m.getY();
        
        boolean hitControlA = false;
        boolean hitControlB = false;
        
        if (this.startHandle.contains(x, y)) {
            hitControlA = true;
        }
        
        if (this.endHandle.contains(x, y)) {
            hitControlB = true;
        }
        
        if (hitControlA & !hitControlB) {
            this.selectStartHandle();
        } else if (!hitControlA & hitControlB) {
            this.selectEndHandle();
        } else {
            // TODO : find a better way to handle this situation!
            System.err.println("Both control handles selected");
        }
        System.err.println("A" + hitControlA + " B" + hitControlB);
    }
    
    @Override
    public void mouseReleased(MouseEvent m) {
        String s = String.format("%.0f %.0f %.0f", slice.getAngleStart(), slice.getAngleExtent(), this.getMax());
        System.err.println(s);
        this.unselectHandles();
    }
    
    @Override
    public void mouseMoved(MouseEvent m) {}
    
    @Override
    public void mouseDragged(MouseEvent m) {
        if (this.startHandleSelected) {
            double sliceShift = this.calculateSliceShift(m, this.slice.getStartPoint());
            double angleStart = this.slice.getAngleStart();
            double angleExtent = this.slice.getAngleExtent();
            this.slice.setAngleStart(angleStart - sliceShift);
            this.slice.setAngleExtent(angleExtent + sliceShift);
            this.positionStartHandle();
            this.positionEndHandle();
            this.repaint();
//            System.err.println(String.format("Start %.2f", angleStart));
        } else if (this.endHandleSelected) {
            double sliceShift = this.calculateSliceShift(m, this.slice.getEndPoint());
            double angleExtent = this.slice.getAngleExtent();
            this.slice.setAngleExtent(angleExtent - sliceShift);
            this.positionStartHandle();
            this.positionEndHandle();
            this.repaint();
//            System.err.println(String.format("End %.2f", angleExtent));
        } else {
            //do nothing
        }
    }
    
    public double calculateSliceShift(MouseEvent m, Point2D handlePoint) {
        Point mousePoint = m.getPoint();
        double distanceDragged = mousePoint.distance(handlePoint);
        int directionMultiplier = this.getDirectionMultiplier(mousePoint, handlePoint);
//        System.err.println(String.format("%.2f", distanceDragged));
        return distanceDragged * 0.1 * directionMultiplier;
    }

    public int getDirectionMultiplier(Point2D p, Point2D handlePoint) {
        Line2D line = new Line2D.Double(this.center, handlePoint);
        
        // 1 for counterclockwise (in normal Java2D coords)
        //-1 for clockwise (in J2D).
        int ccw = line.relativeCCW(p);
        return -ccw;
    }
    
    private class Tic {
        
        private Line2D line;
        
        private String label;
        
        private int labelX;
        
        private int labelY;
        
        public Tic(Line2D line, String label, int labelX, int labelY) {
            this.line = line;
            this.label = label;
            this.labelX = labelX;
            this.labelY = labelY;
        }
        
        public void paint(Graphics2D g2) {
            g2.draw(this.line);
            g2.drawString(this.label, this.labelX, this.labelY);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        javax.swing.JFrame frame =  new javax.swing.JFrame("TorsionDial Test");
        frame.add(new TorsionDial(400, 400));
        frame.pack();
        frame.setVisible(true);
    }

}
