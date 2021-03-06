package tailor.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import tailor.description.Description;
import tailor.description.ProteinDescription;

public class DiagramListCellRenderer extends JLabel implements ListCellRenderer<Description> {
	
	private final int w;
	private final int h;
	private HashMap<Description, ImageIcon> unselectedIconMap;
	private HashMap<Description, ImageIcon> selectedIconMap;
	
	public DiagramListCellRenderer(int w, int h, List<Description> descriptions) {
		this.w = w;
		this.h = h;
		this.unselectedIconMap = new HashMap<>();
		this.selectedIconMap = new HashMap<>();
		for (Description description : descriptions) {
			ResidueDiagram diagram = new ResidueDiagram();
			diagram.createFromDescription((ProteinDescription)description);
			diagram.setDrawLabels(false);
			diagram.fitToSize(w, h);
			
			this.unselectedIconMap.put(description, this.makeUnselectedIcon(diagram));
			this.selectedIconMap.put(description, this.makeSelectedIcon(diagram));
		}
	}
	
	private ImageIcon makeUnselectedIcon(ResidueDiagram diagram) {
		BufferedImage image =
			new BufferedImage(this.w, h, BufferedImage.TYPE_BYTE_INDEXED);

		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, this.w, this.h);

		diagram.paint(g2);
		
		return new ImageIcon(image);
	}
	
	private ImageIcon makeSelectedIcon(ResidueDiagram diagram) {
		BufferedImage image =
			new BufferedImage(this.w, h, BufferedImage.TYPE_BYTE_INDEXED);

		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setBackground(Color.GRAY);
		g2.clearRect(0, 0, this.w, this.h);
		
		diagram.paint(g2);
		
		return new ImageIcon(image);
	}
	
	public Component getListCellRendererComponent(
		       JList<? extends Description> list,
		       Description value,            // value to display
		       int index,               // cell index
		       boolean isSelected,      // is the cell selected
		       boolean cellHasFocus) {   // the list and the cell have the focus
		
		if (isSelected) {
			this.setIcon(this.selectedIconMap.get(value));
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			this.setIcon(this.unselectedIconMap.get(value));
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setOpaque(false);
		return this;
	}

}
