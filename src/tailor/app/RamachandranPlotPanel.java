package tailor.app;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Shape;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import tailor.category.Category;
import tailor.category.filter.Bound;
import tailor.category.filter.BoundShape;

/**
 * Manages a RamachandranPlotCanvas
 * 
 * @author maclean
 *
 */
public class RamachandranPlotPanel extends JPanel {

	private RamachandranPlotCanvas canvas;
	
	public RamachandranPlotPanel() {
		this.canvas = new RamachandranPlotCanvas();
		this.setLayout(new BorderLayout());
		this.add(this.canvas, BorderLayout.CENTER);
		
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	}
    
    public Image getImage() {
        return this.canvas.grabImage();
    }
    
    public int getNumberOfResidues() {
        return this.canvas.numberOfPointLists();
    }
	
	public void addColumns(List<String> ids, List<String>[] columns) {
		if (columns.length % 2 != 0) {
			System.err.println("Trying to add uneven number of columns! : " + columns.length);
            return;
		}
        
        int numberOfResidues = columns.length / 2;

		for (int residueIndex = 0; residueIndex < numberOfResidues; residueIndex++) {
			int row = 0;
            int firstColumnIndex = residueIndex * 2;
            int secondColumnIndex = firstColumnIndex + 1;
			while (row < columns[firstColumnIndex].size()) {
				String id = ids.get(row);
				double x = Double.valueOf((String) columns[firstColumnIndex ].get(row));
				double y = Double.valueOf((String) columns[secondColumnIndex].get(row));
				this.canvas.addPoint(residueIndex, id, x, y);
				row++;
			}
		}
		
		this.canvas.repaint();
	}
    
    public void addRows(List<String[]> rowData, int columnStart, int columnEnd) {
        for (String[] row : rowData) {
            String id = row[0] + row[1];
            int residueIndex = 0;
            for (int i = columnStart; i <= columnEnd; i += 2) {
                double x = Double.valueOf(row[i]);
                double y = Double.valueOf(row[i + 1]);
                this.canvas.addPoint(residueIndex, id, x, y);
                residueIndex++;
            }
        }
        
        this.canvas.repaint();
    }
    
    public void clearPoints() {
        this.canvas.clearPoints();
        this.canvas.repaint();
    }
    
    public void clearBounds() {
        this.canvas.clearBounds();
        this.canvas.repaint();
    }
    
    public void showCategory(Category category) {
        System.out.println("showing category " + category);
        
        // important! - unless we want multiple categories displayed at once...
        this.canvas.clearBounds();
        
        for (Bound bound : category.getBounds()) {
            Shape[] shapes = bound.getShapes();
            for (Shape shape : shapes) {
                this.canvas.addBoundShape(shape);
            }
        }
        this.canvas.selectOnlyCategory(category);
        
        this.canvas.repaint();
    }
    
    /**
     * @param args the data filepath, the output filepath, the columns to use, 
     * and the category.
     *  
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            if (args[0].equals("-h") || args[0].equals("-H") || args[0].equals("--help")) {
                System.err.println("RamachandranPlotPanel <dataFilePath> <outputFilePath>"
                    + " <columnStart> <columnEnd> <categoryBounds>");
                System.err.println("Where <dataFilePath> and <outputFilePath> are paths");
                System.err.println("and <columnStart> and <columnEnd> are integer indices");
                System.err.println("and <categoryBounds> a comma separated list of numbers");
                System.exit(1);
            }
            else {
                System.err.println("Use -h or --help for help");
                System.exit(1);
            }
        }

        String dataFilePath = args[0];
        String outputFilePath = args[1];
        String columnStartIndexString = args[2];
        String columnEndIndexString = args[3];
        String categoryBoundString = args[4];
        
        // read in the data file
        List<String[]> rowData = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(dataFilePath));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] bits = line.split("\t");
                rowData.add(bits);
            }
        } catch (FileNotFoundException f) {
            System.err.println("File not found " + dataFilePath);
            System.exit(1);
        } catch (IOException i) {
            System.err.println("IO error");
            System.exit(1);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
        
        // parse the column numbers and get the data in column form
        int columnStartIndex = 0;
        int columnEndIndex = 0;
        try {
            columnStartIndex = Integer.parseInt(columnStartIndexString);
            columnEndIndex = Integer.parseInt(columnEndIndexString);
        } catch (NumberFormatException nfe) {
            System.err.println("Problem with column indices " + columnStartIndexString
                    + "," + columnEndIndexString);
            System.exit(1);
        }
        
        // parse the bounds
        String[] bits = categoryBoundString.split(",");
        Category category = new Category("name");
        int columnIndex = 2;
        for (int b = 0; b < bits.length; b += 4) {
            try {
                double xmin = Double.parseDouble(bits[b]);
                double xmax = Double.parseDouble(bits[b + 1]);
                double ymin = Double.parseDouble(bits[b + 2]);
                double ymax = Double.parseDouble(bits[b + 3]);
                Bound bound = new Bound(columnIndex, columnIndex + 1, 
                		xmin, xmax, ymin, ymax, BoundShape.RECTANGULAR);
                category.addBound(bound);
                columnIndex += 2;
            } catch (NumberFormatException n) {
                System.err.println("Problem with bound definitions " + categoryBoundString);
                System.exit(1);
            }
        }
        
        // finally, setup the plot
        RamachandranPlotPanel plot = new RamachandranPlotPanel();
        plot.addRows(rowData, columnStartIndex, columnEndIndex);
        plot.showCategory(category);
        
        JFrame frame = new JFrame();
        frame.add(plot);
        frame.pack();
        
        try {
            ImageIO.write((RenderedImage) plot.getImage(), "JPG", new File(outputFilePath));
        } catch (IOException ioe) {
            System.err.println(ioe);
            System.exit(1);
        }
        
        System.exit(0);
    }
 
}
