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

import tailor.description.ChainDescription;

public class DescriptionList extends JPanel {
	
	private App app;
	private JList<ChainDescription> list;
	
	public DescriptionList(Border border, App app) {
		this.list = new JList<>(new DefaultListModel<>());
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
	
	public void replace(ChainDescription oldDescription, ChainDescription newDescription) {
		this.getListModel().removeElement(oldDescription);
    	this.getListModel().addElement(newDescription);
	}
	
	public void fireEditEvent() {
		ChainDescription d = this.getSelectedDescription();
		ChainDescription edited = this.app.editDescription(d);
        if (!edited.equals(d)) {
        	this.replace(d, edited);
        }
	}
	
	public void addListSelectionListener(ListSelectionListener listener) {
		this.list.addListSelectionListener(listener);
	}
	
	public void add(ChainDescription description) {
		this.getListModel().addElement(description);
	}
	
	public void clear() {
		this.getListModel().clear();
	}
	
	public ChainDescription[] getAllDescriptions() {
		return (ChainDescription[]) this.getListModel().toArray();
	}
	
	public ChainDescription getSelectedDescription() {
		return this.getListModel().getElementAt(this.list.getSelectedIndex());
	}

	 public DefaultListModel<ChainDescription> getListModel() {
		 return (DefaultListModel<ChainDescription>) this.list.getModel();
	 }

}
