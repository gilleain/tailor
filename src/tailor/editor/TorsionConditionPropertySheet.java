package tailor.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tailor.condition.TorsionBoundCondition;
import tailor.description.DescriptionFactory;

public class TorsionConditionPropertySheet extends JPanel implements ActionListener {
	
	private JTextField nameField;
	private JTextField minValue;
	private JTextField maxValue;
	
	private JLabel atomALabel;
	private JLabel atomBLabel;
	private JLabel atomCLabel;
	private JLabel atomDLabel;
	
	private JButton updateButton;
	private JButton revertButton;
	
	private DescriptionFactory descriptionFactory;
	
	public TorsionConditionPropertySheet(DescriptionFactory descriptionFactory) {
	    this.descriptionFactory = descriptionFactory;
	    
		this.setLayout(new BorderLayout());
		this.add(new JLabel("Torsion Condition", JLabel.CENTER), BorderLayout.NORTH);

		JPanel mainPanel = new JPanel(new GridLayout(7, 2));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        mainPanel.add(new JLabel("Name:"));
        
        this.nameField = new JTextField(4);
        mainPanel.add(this.nameField);
        
        mainPanel.add(new JLabel("Min Value:"));
        this.minValue = new JTextField();
        mainPanel.add(this.minValue);
        
        mainPanel.add(new JLabel("Max Value:"));
        this.maxValue = new JTextField();
        mainPanel.add(this.maxValue);
        
        mainPanel.add(new JLabel("Atom A"));
        this.atomALabel = new JLabel();
        mainPanel.add(this.atomALabel);
        
        mainPanel.add(new JLabel("Atom B"));
        this.atomBLabel = new JLabel();
        mainPanel.add(this.atomBLabel);
        
        mainPanel.add(new JLabel("Atom C"));
        this.atomCLabel = new JLabel();
        mainPanel.add(this.atomCLabel);
        
        mainPanel.add(new JLabel("Atom D"));
        this.atomDLabel = new JLabel();
        mainPanel.add(this.atomDLabel);
        
        this.atomALabel.setHorizontalAlignment(JLabel.CENTER);
		this.atomBLabel.setHorizontalAlignment(JLabel.CENTER);
		this.atomCLabel.setHorizontalAlignment(JLabel.CENTER);
		this.atomDLabel.setHorizontalAlignment(JLabel.CENTER);
        
        this.add(mainPanel, BorderLayout.CENTER);
        
//        JPanel buttonPanel = new JPanel();
//        
//		this.updateButton = new JButton("Update");
//		this.updateButton.setActionCommand("Update");
//		this.updateButton.addActionListener(this);
//		buttonPanel.add(this.updateButton);
//
//		this.revertButton = new JButton("Revert");
//		this.revertButton.setActionCommand("Revert");
//		this.revertButton.addActionListener(this);
//		buttonPanel.add(this.revertButton);
//
//		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public void setToUnknownValues(String torsionName) {
		this.minValue.setText("-180");
		this.maxValue.setText("180");
		if (torsionName.equals("phi")) {
			this.nameField.setText("phi(i)");
			this.atomALabel.setText("i - 1.C");
			this.atomBLabel.setText("i.N");
			this.atomCLabel.setText("i.CA");
			this.atomDLabel.setText("i.C");
		} else if (torsionName.equals("psi")) {
			this.nameField.setText("psi(i)");
			this.atomALabel.setText("i.N");
			this.atomBLabel.setText("i.CA");
			this.atomCLabel.setText("i.C");
			this.atomDLabel.setText("i + 1.N");
		} else if (torsionName.equals("omega")) {
			this.nameField.setText("omega(i/i + 1)");
			this.atomALabel.setText("i.CA");
			this.atomBLabel.setText("i.C");
			this.atomCLabel.setText("i + 1.N");
			this.atomDLabel.setText("i + 1.CA");
		}
	}
	
	public void setValues(String torsionName, int startResidueNumber, int endResidueNumber) {
		if (torsionName.equals("phi")) {
			this.nameField.setText("phi(" + endResidueNumber + ")");
			this.atomALabel.setText(startResidueNumber + ".C");
			this.atomBLabel.setText(endResidueNumber + ".N");
			this.atomCLabel.setText(endResidueNumber + ".CA");
			this.atomDLabel.setText(endResidueNumber + ".C");
		} else if (torsionName.equals("psi")) {
			this.nameField.setText("psi(" + startResidueNumber + ")");
			this.atomALabel.setText(startResidueNumber + ".N");
			this.atomBLabel.setText(startResidueNumber + ".CA");
			this.atomCLabel.setText(startResidueNumber + ".C");
			this.atomDLabel.setText(endResidueNumber + ".N");
		} else if (torsionName.equals("omega")) {
			this.nameField.setText("omega(" + startResidueNumber + "/" + endResidueNumber + ")");
			this.atomALabel.setText(startResidueNumber + ".CA");
			this.atomBLabel.setText(startResidueNumber + ".C");
			this.atomCLabel.setText(endResidueNumber + ".N");
			this.atomDLabel.setText(endResidueNumber + ".CA");
		}
	}
	
	public TorsionBoundCondition getCondition(int residueNumber) {
		double min = Double.parseDouble(this.minValue.getText());
		double max = Double.parseDouble(this.maxValue.getText());
		double range = (max - min) / 2;
		double midPoint = (max + min) / 2;
		String torsionName = this.nameField.getText();
		
		if (torsionName.startsWith("phi")) {
		    return descriptionFactory.createPhiCondition(midPoint, range, residueNumber, "A");
		} else if (torsionName.startsWith("psi")) {
		    return descriptionFactory.createPsiCondition(midPoint, range, residueNumber, "A");
		} else if (torsionName.equals("omega")) {
		    // XXX this won't work for omega, as we need start/end!
		    return null; 
		} else {
		    return null;  // XX throw error
		}
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		 String command = ae.getActionCommand();
		 if (command.equals("Update")) {
		     System.err.println("update");
		 } else if (command.equals("Revert")) {
		     System.err.println("revert");
		 }
	}

}
