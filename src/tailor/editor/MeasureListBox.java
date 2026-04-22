package tailor.editor;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import tailor.api.AtomListMeasure;

public class MeasureListBox extends JPanel {
	
	private JList<AtomListMeasure> measureList;
	
	public MeasureListBox() {
		
		this.measureList = new JList<>(new DefaultListModel<>());
		this.measureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setPreferredSize(new Dimension(200, 200));
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(new JLabel("Measures"));
		this.add(new JScrollPane(this.measureList));
	}
	
	public void addMeasureToList(AtomListMeasure measure) {
		this.getListModel().addElement(measure);
		this.measureList.setSelectedValue(measure, true);
		System.err.println("adding " + measure);
	}
	
	public void removeSelectedCondition() {
		Object selected = this.measureList.getSelectedValue();
		this.getListModel().removeElement(selected);
		this.measureList.setSelectedIndex(this.getListModel().size() - 1);
	}
	
	public DefaultListModel<AtomListMeasure> getListModel() { 
		return (DefaultListModel<AtomListMeasure>) this.measureList.getModel();
	}
	
	public List<AtomListMeasure> getMeasures() {
		List<AtomListMeasure> measures = new ArrayList<>();
		DefaultListModel<AtomListMeasure> model = this.getListModel();
		for (int i = 0; i < model.size(); i++) {
			measures.add(model.get(i));
		}
		return measures;
	}
    
    public void setMeasures(List<AtomListMeasure> list) {
        for (AtomListMeasure measure : list) {
            this.addMeasureToList(measure);
        }
    }
    
    public void clear() {
    	this.getListModel().clear();
    }
}
