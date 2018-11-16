package tailor.app;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import tailor.description.Description;
import tailor.description.ProteinDescription;
import tailor.editor.ResidueDiagram;

public class DescriptionPanel extends JPanel {
	
	private Description description;
	private ResidueDiagram diagram;
	
	public DescriptionPanel(MouseListener listener) {
		 this.addMouseListener(listener);
		 this.description = null;
		 this.diagram = new ResidueDiagram();
		 this.setPreferredSize(new Dimension(300, 200));
	}
	
	public void clear() {
		this.description = null;
		this.diagram = new ResidueDiagram();
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		int w = this.getWidth();
		int h = this.getHeight();
		g.clearRect(0, 0, w, h);
		if (this.description == null) {
			g.drawString("No Motif Loaded", (w / 2) - 50, h / 2);
		} else {
			this.diagram.paint(g);
		}
	}
	
	public void setEdited(Description edited) {
        if (!edited.equals(this.description)) {
        	this.setDescription(edited);
        	this.repaint();
        }
	}
	
	public boolean hasDescription() {
		return this.description != null;
	}
	
	public Description getDescription() {
		return this.description;
	}
	
	public void setDescription(Description description) {
		this.description = description;
		this.diagram = new ResidueDiagram((ProteinDescription) description);	// XXX
		this.diagram.setDrawLabels(false);
		this.diagram.fitToSize(this.getWidth(), this.getHeight());
		
		this.repaint();
	}

}
