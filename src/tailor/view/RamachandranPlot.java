package tailor.view;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Note auto-generated from python code using Claude AI
 * 
 * A module to make Ramachandran plots from lists of torsions.
 */
public class RamachandranPlot {
    
    // Plot properties
    private int x = 30;
    private int y = 30;
    private int width = 360;
    private int height = 360;
    private boolean visibleGrid = false;
    private int gridSpacing = 30;
    private Color axesColor = Color.BLACK;
    private List<List<Point2D>> data;
    private List<Color> pointSetColors;
    
    public RamachandranPlot() {
        this.data = new ArrayList<>();
        this.pointSetColors = new ArrayList<>();
        pointSetColors.add(Color.BLUE);
        pointSetColors.add(Color.RED);
        pointSetColors.add(Color.GREEN);
        pointSetColors.add(new Color(128, 0, 128)); // Purple
    }
    
    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setVisibleGrid(boolean visible) { this.visibleGrid = visible; }
    public void setGridSpacing(int spacing) { this.gridSpacing = spacing; }
    public void setAxesColor(Color color) { this.axesColor = color; }
    public void setData(List<List<Point2D>> data) { this.data = data; }
    
    public BufferedImage draw() {
        BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        // Enable antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fill background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 400, 400);
        
        // Draw grid if visible
        if (visibleGrid) {
            drawGrid(g);
        }
        
        // Draw internal axes
        g.setColor(axesColor);
        g.drawLine(x + 180, y, x + 180, y + height); // Vertical axis at phi=0
        g.drawLine(x, y + 180, x + width, y + 180);  // Horizontal axis at psi=0
        
        // Draw external axes
        g.setColor(Color.BLACK);
        // X axis (bottom)
        g.drawLine(x, y, x + width, y);
        // Y axis (left)
        g.drawLine(x, y, x, y + height);
        // Top
        g.drawLine(x, y + height, x + width, y + height);
        // Right
        g.drawLine(x + width, y, x + width, y + height);
        
        // Draw tick marks and labels
        drawTickMarks(g);
        
        // Draw axis labels
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(Color.BLACK);
        g.drawString("Φ°", x + 180 - 10, y - 5); // Phi label
        g.drawString("Ψ°", x - 25, y + 180 + 5); // Psi label
        
        // Draw data points
        for (int i = 0; i < data.size(); i++) {
            Color color = i < pointSetColors.size() ? pointSetColors.get(i) : Color.BLACK;
            g.setColor(color);
            
            for (Point2D point : data.get(i)) {
                double phi = point.phi;
                double psi = point.psi;
                
                int phiAsX = (int)(phi + 180 + x);
                int psiAsY = (int)(psi + 180 + y);
                
                g.fillRect(phiAsX, psiAsY, 2, 2);
            }
        }
        
        g.dispose();
        return image;
    }
    
    private void drawGrid(Graphics2D g) {
        g.setColor(Color.LIGHT_GRAY);
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(0.5f));
        
        // Vertical grid lines
        for (int i = -180; i <= 180; i += gridSpacing) {
            int xPos = x + i + 180;
            g.drawLine(xPos, y, xPos, y + height);
        }
        
        // Horizontal grid lines
        for (int i = -180; i <= 180; i += gridSpacing) {
            int yPos = y + i + 180;
            g.drawLine(x, yPos, x + width, yPos);
        }
        
        g.setStroke(oldStroke);
    }
    
    private void drawTickMarks(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        
        // X axis tick marks and labels
        for (int i = -180; i <= 180; i += gridSpacing) {
            int xPos = x + i + 180;
            g.drawLine(xPos, y, xPos, y - 5);
            
            String label = String.valueOf(i);
            FontMetrics fm = g.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            g.drawString(label, xPos - labelWidth / 2, y - 8);
        }
        
        // Y axis tick marks and labels
        for (int i = -180; i <= 180; i += gridSpacing) {
            int yPos = y + i + 180;
            g.drawLine(x, yPos, x - 5, yPos);
            
            String label = String.valueOf(i);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(label, x - fm.stringWidth(label) - 8, yPos + 4);
        }
    }
    
    public void saveToPNG(String filename) throws IOException {
        BufferedImage image = draw();
        ImageIO.write(image, "PNG", new File(filename));
    }
    
    public static BufferedImage demo() {
        List<List<Point2D>> points = new ArrayList<>();
        
        // Point set 1
        List<Point2D> set1 = new ArrayList<>();
        set1.add(new Point2D(-45, -45));
        set1.add(new Point2D(-30, -37));
        set1.add(new Point2D(-60, -20));
        points.add(set1);
        
        // Point set 2
        List<Point2D> set2 = new ArrayList<>();
        set2.add(new Point2D(-45, 145));
        set2.add(new Point2D(-30, 137));
        set2.add(new Point2D(-60, 120));
        points.add(set2);
        
        RamachandranPlot plot = new RamachandranPlot();
        plot.setX(30);
        plot.setY(30);
        plot.setData(points);
        plot.setVisibleGrid(true);
        plot.setGridSpacing(30);
        plot.setAxesColor(Color.RED);
        
        return plot.draw();
    }
    
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            // Demo mode
            BufferedImage image = demo();
            ImageIO.write(image, "PNG", new File("demo.png"));
            System.out.println("Demo plot saved to demo.png");
            return;
        }
        
        if (args.length < 3) {
            System.err.println("Usage: java RamachandranPlot <filename> <columnStart> <columnEnd>");
            System.exit(1);
        }
        
        String filename = args[0];
        int columnStart = Integer.parseInt(args[1]);
        int columnEnd = Integer.parseInt(args[2]);
        
        BufferedReader reader;
        if (filename.equals("-")) {
            reader = new BufferedReader(new InputStreamReader(System.in));
            filename = "stdin";
        } else {
        	File file = new File(filename);
            reader = new BufferedReader(new FileReader(file));
        }
        
        int numberOfPointSets = ((columnEnd - columnStart) + 1) / 2;
        List<List<Point2D>> data = new ArrayList<>();
        
        for (int i = 0; i < numberOfPointSets; i++) {
            data.add(new ArrayList<>());
        }
        
        String line;
        boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                continue; // Skip header
            }
            
            String[] parts = line.split("\t");
            List<Double> angles = new ArrayList<>();
            
            for (int i = columnStart; i <= columnEnd && i < parts.length; i++) {
                try {
                    angles.add(Double.parseDouble(parts[i]));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing number in line: " + line);
                    continue;
                }
            }
            
            for (int i = 0; i < numberOfPointSets; i++) {
                int idx = i * 2;
                try {
                    data.get(i).add(new Point2D(angles.get(idx), angles.get(idx + 1)));
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Index error in: " + line.trim());
                }
            }
        }
        
        reader.close();
        
        RamachandranPlot plot = new RamachandranPlot();
        plot.setX(30);
        plot.setY(30);
        plot.setVisibleGrid(false);
        plot.setGridSpacing(30);
        plot.setData(data);
        
        plot.saveToPNG(filename + ".png");
        System.out.println("Plot saved to " + filename + ".png");
    }
    
    // Helper class for 2D points
    static class Point2D {
        double phi;
        double psi;
        
        Point2D(double phi, double psi) {
            this.phi = phi;
            this.psi = psi;
        }
    }
}
