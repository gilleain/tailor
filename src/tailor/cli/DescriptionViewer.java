package tailor.cli;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.cli.ParseException;

import tailor.datasource.xml.XmlDescriptionReader;
import tailor.description.ChainDescription;
import tailor.view.ResidueDiagram;

public class DescriptionViewer {
	
	private static class DescriptionPanel extends JPanel {
		private static final int MIN_WIDTH = 920;
		private static final int MIN_HEIGHT = 300;
		private final ResidueDiagram residueDiagram;
		
		public DescriptionPanel(ChainDescription chainDescription) {
			this.residueDiagram = new ResidueDiagram(chainDescription);
			int dw = this.residueDiagram.getWidth();
			int dh = this.residueDiagram.getHeight();
			int canvasWidth = Math.max(MIN_WIDTH, dw);
			int canvasHeight = Math.max(MIN_HEIGHT, dh);
			setPreferredSize(new Dimension(canvasWidth, canvasHeight));
			this.repaint();
		}
		
		@Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.residueDiagram.paint(g);
		}
	}
	
	private static class DescriptionFrame extends JFrame {
		
		private final ChainDescription chainDescription;
		
		public DescriptionFrame(ChainDescription chainDescription) {
			super("Description");	// TODO - use name
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.chainDescription = chainDescription;
			DescriptionPanel descriptionPanel = new DescriptionPanel(chainDescription);
			add(descriptionPanel);
			pack();
			setLocationRelativeTo(null);
		}
		
	}

	 public static void main(String[] args) throws ParseException, IOException {
	        System.err.println("Starting...");
	        CommandLineHandler handler = new CommandLineHandler().processArguments(args);
	        
	        if (handler.getDescriptionFileName() != null) {
	        	final ChainDescription chainDescription = read(handler.getDescriptionFileName());
	        	SwingUtilities.invokeLater(() -> new DescriptionFrame(chainDescription).setVisible(true));
	        } else {
	        	System.err.println("Please provide a description file");
	        }
	        
	 }

	 private static ChainDescription read(String filename) {
		 XmlDescriptionReader reader = new XmlDescriptionReader();
		 return reader.readDescription(new File(filename));
	 }
}
