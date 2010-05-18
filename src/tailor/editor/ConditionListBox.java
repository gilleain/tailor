package tailor.editor;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import tailor.description.Description;
import tailor.condition.Condition;

public class ConditionListBox extends JPanel {
	
	private JList conditionList;
	
	public ConditionListBox() {
		
		this.conditionList = new JList(new DefaultListModel());
		this.conditionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setPreferredSize(new Dimension(200, 200));
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(new JLabel("Conditions", JLabel.LEFT));
		this.add(new JScrollPane(this.conditionList));
	}
	
	public void addConditionToList(Condition condition) {
		this.getListModel().addElement(condition);
		this.conditionList.setSelectedValue(condition, true);
		System.err.println("adding " + condition);
	}
	
	public void removeSelectedCondition() {
		Object selected = this.conditionList.getSelectedValue();
		this.getListModel().removeElement(selected);
		this.conditionList.setSelectedIndex(this.getListModel().size() - 1);
	}
	
	public DefaultListModel getListModel() { 
		return (DefaultListModel) this.conditionList.getModel();
	}
    
    public void setConditions(Description description) {
        for (Condition condition : description.getConditions()) {
            this.addConditionToList(condition);
        }
    }
	
}
