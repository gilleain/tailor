package tailor.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import tailor.structure.Level;

/**
 * @author maclean
 *
 */
public class DescriptionTreeControls extends JPanel implements ActionListener, TreeSelectionListener {
	
	public DescriptionTreeView tree;
	
	private JToolBar toolBar;
    
    private JLabel levelLabel;
    
    private JTextField nameField;
    
	private JButton addLevelButton;
    
	private JButton removeLevelButton;
 
	public DescriptionTreeControls(DescriptionTreeView tree) {
		this.tree = tree;
		this.tree.addTreeSelectionListener(this);
		
		this.toolBar = new JToolBar();
		this.toolBar.setFloatable(false);
        
        this.levelLabel = new JLabel("Motif Name");
        this.nameField = new JTextField(10);
		
		this.addLevelButton = new JButton("Add");
		this.addLevelButton.setActionCommand("ADD");
		this.addLevelButton.addActionListener(this);
        
		this.removeLevelButton = new JButton("Remove");
		this.removeLevelButton.setActionCommand("REMOVE");
		this.removeLevelButton.addActionListener(this);
        
        this.updateButtons();
        
        this.toolBar.add(this.levelLabel);
        this.toolBar.addSeparator();
        this.toolBar.add(this.nameField);
		this.toolBar.add(this.addLevelButton);
        this.toolBar.addSeparator();
		this.toolBar.add(this.removeLevelButton);
  
		this.add(this.toolBar);
	}
	
	public void updateButtons() {
        Level level = this.tree.getCurrentlySelectedLevel();
        
        String levelName; 
        if (level == null) {
            levelName = null;
        } else {
            levelName = level.toString();
            levelName = this.titleCase(levelName);
        }
		
        String lowerLevelName;
        if (level == null) {
            lowerLevelName = "Motif";   // XXX : hmmm. clashes with "Protein" - unsure...
        } else {
            lowerLevelName = this.tree.getLevelBelowCurrentlySelectedLevel().toString();
            lowerLevelName = this.titleCase(lowerLevelName);
        }
        
        // the LEVEL label
        if (level == null) {
            this.levelLabel.setText("Motif Name");
        } else {
            this.levelLabel.setText(lowerLevelName +" Name");
        }
        
        // the NAME field
        this.nameField.setText("");
		
        // the ADD button
        if (levelName != null && levelName.equals("Atom")) {
            this.addLevelButton.setEnabled(false);
        } else {
            this.addLevelButton.setEnabled(true);
            this.addLevelButton.setText("Add " + lowerLevelName);
        }
        
        // the REMOVE button
        if (levelName == null) {
            this.removeLevelButton.setText("Can't Remove");
            this.removeLevelButton.setEnabled(false);
        } else {
            this.removeLevelButton.setEnabled(true);
            this.removeLevelButton.setText("Remove " + levelName);
        }
	}
    
    private String titleCase(String string) {
        return string.substring(0, 1) + string.substring(1).toLowerCase();
    }
	
	public void actionPerformed(ActionEvent ae) {
		String actionCommand = ae.getActionCommand();
		if (actionCommand.equals("ADD")) {
            String name = this.nameField.getText();
			this.tree.addDescriptionToChildOfCurrentlySelectedNode(name, -1);
		} else if (actionCommand.equals("REMOVE")) {
			this.tree.deleteSelectedDescription();
		} 
        
	}

	public void valueChanged(TreeSelectionEvent e) {
		this.updateButtons();
	}
	
}
