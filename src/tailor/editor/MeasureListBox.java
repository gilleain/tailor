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

import tailor.measurement.Measure;

public class MeasureListBox extends JPanel {
	
	private JList<Measure<?>> measureList;
	
	public MeasureListBox() {
		
		this.measureList = new JList<Measure<?>>(new DefaultListModel<Measure<?>>());
		this.measureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setPreferredSize(new Dimension(200, 200));
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(new JLabel("Measures"));
		this.add(new JScrollPane(this.measureList));
	}
	
	public void addMeasureToList(Measure<?> measure) {
		this.getListModel().addElement(measure);
		this.measureList.setSelectedValue(measure, true);
		System.err.println("adding " + measure);
	}
	
	public void removeSelectedCondition() {
		Object selected = this.measureList.getSelectedValue();
		this.getListModel().removeElement(selected);
		this.measureList.setSelectedIndex(this.getListModel().size() - 1);
	}
	
	public DefaultListModel<Measure<?>> getListModel() { 
		return (DefaultListModel<Measure<?>>) this.measureList.getModel();
	}
	
	public List<Measure<?>> getMeasures() {
		List<Measure<?>> measures = new ArrayList<Measure<?>>();
		DefaultListModel<Measure<?>> model = this.getListModel();
		for (int i = 0; i < model.size(); i++) {
			measures.add(model.get(i));
		}
		return measures;
	}
    
    public void setMeasures(List<Measure<?>> list) {
        for (Measure<?> measure : list) {
            this.addMeasureToList(measure);
        }
    }
    
    public void clear() {
    	this.getListModel().clear();
    }
}
