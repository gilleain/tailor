package tailor.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DiagramPropertyPanel extends JPanel {
	
	private JTextField nameField;
	private JLabel residueNumberLabel;
	private JLabel hBondConditionCountLabel;
	private JLabel torsionConditionCountLabel;
	
	public DiagramPropertyPanel(String motifName, int numberOfResidues) {
		this.setLayout(new BorderLayout());
		this.add(new JLabel("Diagram Properties", JLabel.CENTER), BorderLayout.NORTH);

		JPanel mainPanel = new JPanel(new GridLayout(4, 2));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
		
		mainPanel.add(new JLabel("Name", JLabel.LEFT));
		this.nameField = new JTextField(motifName);
		mainPanel.add(this.nameField);
		
		mainPanel.add(new JLabel("Residues", JLabel.LEFT));
		this.residueNumberLabel = new JLabel(String.valueOf(numberOfResidues), JLabel.CENTER);
		mainPanel.add(this.residueNumberLabel);
		
		mainPanel.add(new JLabel("HBonds", JLabel.LEFT));
		this.hBondConditionCountLabel = new JLabel("0", JLabel.CENTER);
		mainPanel.add(this.hBondConditionCountLabel);
		
		mainPanel.add(new JLabel("Torsions", JLabel.LEFT));
		this.torsionConditionCountLabel = new JLabel("0", JLabel.CENTER);
		mainPanel.add(this.torsionConditionCountLabel);
		
		this.add(mainPanel, BorderLayout.CENTER);
	}
	
	public void setValues(ResidueDiagram diagram) {
		this.nameField.setText(diagram.getName());
		this.setNumberOfResidues(diagram.getNumberOfResidues());
	}
	
	public void incrementNumberOfResidues() {
		// this is a bit foolish, really...
		int n = Integer.valueOf(this.residueNumberLabel.getText()); 
		this.residueNumberLabel.setText(String.valueOf(n + 1));
	}
	
	public void decrementNumberOfResidues() {
		// this is a bit foolish, really...
		int n = Integer.valueOf(this.residueNumberLabel.getText()); 
		this.residueNumberLabel.setText(String.valueOf(n - 1));
	}
	
	public void setNumberOfResidues(int numberOfResidues) {
		this.residueNumberLabel.setText(String.valueOf(numberOfResidues));
	}
	
	public void incrementNumberOfHBondConditions() {
		int numberOfHBondConditions = Integer.parseInt(this.hBondConditionCountLabel.getText()); 
		this.hBondConditionCountLabel.setText(String.valueOf(numberOfHBondConditions + 1));
	}
	
	public void incrementNumberOfTorsionConditions() {
		int numberOfTorsionConditions = Integer.parseInt(this.torsionConditionCountLabel.getText());
		this.torsionConditionCountLabel.setText(String.valueOf(numberOfTorsionConditions + 1));
	}

}
