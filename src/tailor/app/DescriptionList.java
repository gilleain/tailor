package tailor.app;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionListener;

import tailor.description.Description;

public class DescriptionList extends JPanel {
	
	private App app;
	private JList<Description> list;
	
	public DescriptionList(Border border, App app) {
		this.list = new JList<Description>(new DefaultListModel<Description>());
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.add(this.list);
		
		this.app = app;
		MouseListener mouseListener = new MouseAdapter() {
		    
		     @Override
		     public void mouseClicked(MouseEvent e) {
		         if (e.getClickCount() == 2) {
		             int index = list.locationToIndex(e.getPoint());
		             System.out.println("Double clicked on Item " + index);
		             fireEditEvent();
		          }
		     }
		 };
		 list.addMouseListener(mouseListener);
		
		this.setPreferredSize(new Dimension(100, 300));
		this.setBorder(border);
	}
	
	public void replace(Description oldDescription, Description newDescription) {
		this.getListModel().removeElement(oldDescription);
    	this.getListModel().addElement(newDescription);
	}
	
	public void fireEditEvent() {
		Description d = this.getSelectedDescription();
        Description edited = this.app.editDescription(d);
        if (!edited.equals(d)) {
        	this.replace(d, edited);
        }
	}
	
	public void addListSelectionListener(ListSelectionListener listener) {
		this.list.addListSelectionListener(listener);
	}
	
	public void add(Description description) {
		this.getListModel().addElement(description);
	}
	
	public void clear() {
		this.getListModel().clear();
	}
	
	public Description[] getAllDescriptions() {
		return (Description[]) this.getListModel().toArray();
	}
	
	public Description getSelectedDescription() {
		return (Description) this.getListModel().getElementAt(this.list.getSelectedIndex());
	}

	 public DefaultListModel<Description> getListModel() {
		 return (DefaultListModel<Description>) this.list.getModel();
	 }

}
