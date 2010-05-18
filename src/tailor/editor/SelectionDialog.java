package tailor.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import tailor.description.Description;


/**
 * @author maclean
 *
 */
public class SelectionDialog extends JDialog implements ActionListener, TreeSelectionListener {
    
    private DescriptionTreeView tree;
    
    private JPanel controlPanel;
    
    private JPanel radioButtonPanel;
    
    private ButtonGroup radioButtonGroup;
    
    private JRadioButton[] radioButtons;
    
    private ArrayList<Description> selectedDescriptions;
    
    private JComboBox typeSelector;
    
    private JButton okayButton;
    
    private JButton cancelButton;
    
    private boolean isComplete;
    
    public SelectionDialog(Description description, JDialog parent, String title) {
        super(parent, title, true);
        
        this.tree = new DescriptionTreeView(description);
        this.tree.expandAll();
        this.tree.addTreeSelectionListener(this);
        this.tree.setSize(150, 300);
        this.add(this.tree, BorderLayout.WEST);
        
        this.controlPanel = new JPanel(new BorderLayout());
        
        String[] typeNames = { "Distance", "Angle", "Torsion" };
        
        this.typeSelector = new JComboBox(typeNames);
        this.typeSelector.setActionCommand("CHANGE");
        this.typeSelector.addActionListener(this); 

        JPanel typePanel = new JPanel();
        typePanel.add(new JLabel("Type", JLabel.CENTER));
        typePanel.add(this.typeSelector);
        this.controlPanel.add(typePanel, BorderLayout.NORTH);
        
        this.radioButtonPanel = new JPanel();
        this.setInputArea(2);   // Distance
        this.controlPanel.add(this.radioButtonPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        
        this.okayButton = new JButton("Ok");
        this.okayButton.setActionCommand("OK");
        this.okayButton.setEnabled(false);
        this.okayButton.addActionListener(this);
        buttonPanel.add(this.okayButton);
        
        this.cancelButton = new JButton("Cancel");
        this.cancelButton.setActionCommand("CANCEL");
        this.cancelButton.addActionListener(this);
        buttonPanel.add(this.cancelButton);
        
        this.add(this.controlPanel, BorderLayout.EAST);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.pack();
        this.setLocation(200, 200);
        
        this.selectedDescriptions = new ArrayList<Description>();
        this.selectedDescriptions.add(null);
        this.selectedDescriptions.add(null);
        
        this.isComplete = false;
    }
    
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        if (command.equals("OK")) {
            this.isComplete = true;
            this.setVisible(false);
        } else if (command.equals("CANCEL")) {
            this.isComplete = false;
            this.setVisible(false);
        } else if (command.equals("SET")) { 
            this.tree.clearSelection();
        } else if (command.equals("CHANGE")) {
            int n = this.getNumberOfSelections();
            this.setInputArea(n);
            
            // FIXME : this is deeply horrible 
            int s = this.selectedDescriptions.size();
            if (n > s) {
                // minus one to map number->index
                for (int i = s - 1; i < n; i++) {
                    this.selectedDescriptions.add(null);
                }
            }
        }
    }
    
    public void valueChanged(TreeSelectionEvent tse) {
        // ask the tree for a new path object
        Description path = this.tree.createPathToSelected();
        
        if (path != null) {
            int buttonIndex = getSelectedRadioButtonIndex();
            this.selectedDescriptions.set(buttonIndex, path);
            this.radioButtons[buttonIndex].setText(path.toPathString());
            this.checkCompletionStatus();
        }
    }
    
    private void setInputArea(int n) {
        this.radioButtonPanel.removeAll();
        this.radioButtonPanel.setLayout(new GridLayout(n, 1));
        
        this.radioButtons = new JRadioButton[n];
        this.radioButtonGroup = new ButtonGroup();
//        JLabel[] labels = new JLabel[n];
        for (int i = 0; i < n; i++) {
//            labels[i] = new JLabel(String.valueOf(i + 1), JLabel.RIGHT);
//            this.radioButtonPanel.add(labels[i]);
            
            this.radioButtons[i] = new JRadioButton("Not Set!");
            this.radioButtons[i].setActionCommand("SET");
            this.radioButtons[i].addActionListener(this);
            this.radioButtonPanel.add(this.radioButtons[i]);
            
            this.radioButtonGroup.add(this.radioButtons[i]);
        }
        this.radioButtonGroup.setSelected(this.radioButtons[0].getModel(), true);
        this.radioButtonPanel.setPreferredSize(new Dimension(200, 300));
        this.pack();
    }
    
    private int getSelectedRadioButtonIndex() {
        for (int i = 0; i < this.radioButtons.length; i++) {
            JRadioButton r = this.radioButtons[i];
            if (r.isSelected()) {
                return i;
            }
        }
        // FIXME : should really throw an error..
        return -1;
    }
    
    /**
     * Ensures that the user cannot click the ok button without
     * a full complement of selected descriptions
     */
    private void checkCompletionStatus() {
        int n = this.getNumberOfSelections();
        
        // TODO : make sure that the selectedDescription list is maintained properly
        if (n > this.selectedDescriptions.size()) {
            return;
        }
        this.okayButton.setEnabled(true);
    }
    
    public int getNumberOfSelections() {
        String type = this.getCurrentlySelectedType();
        if (type.equals("Distance")) {
            return 2;
        } else if (type.equals("Angle")) {
            return 3;
        } else if (type.equals("Torsion")) {
            return 4;
        } else {
            return -1;  // TODO : this is a little silly
        }
    }
    
    public String getCurrentlySelectedType() {
        return (String) this.typeSelector.getSelectedItem();
    }
    
    public ArrayList<Description> getDescriptions() {
        return this.selectedDescriptions;
    }
    
    public boolean isComplete() {
        return this.isComplete;
    }

}
