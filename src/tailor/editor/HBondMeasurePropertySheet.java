package tailor.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tailor.description.DescriptionFactory;
import tailor.measurement.HBondMeasure;

public class HBondMeasurePropertySheet extends JPanel implements ActionListener {
	
	private JLabel atomALabel;
	private JLabel atomBLabel;
	private JLabel atomCLabel;
	private JLabel atomDLabel;
	
	private JButton updateButton;
	private JButton revertButton;
	
	private DescriptionFactory descriptionFactory;
	
	public HBondMeasurePropertySheet(DescriptionFactory descriptionFactory) {
		
	    this.descriptionFactory = descriptionFactory;
	    
		this.setLayout(new BorderLayout());
		this.add(new JLabel("Hydrogen Bond Measure", JLabel.CENTER), BorderLayout.NORTH);
		
		JPanel mainPanel = new JPanel(new GridLayout(5, 2));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        
        mainPanel.add(new JLabel("Donor Atom"));
        this.atomALabel = new JLabel();
        mainPanel.add(this.atomALabel);
        
        mainPanel.add(new JLabel("Donor Hydrogen"));
        this.atomBLabel = new JLabel();
        mainPanel.add(this.atomBLabel);
        
        mainPanel.add(new JLabel("Acceptor Atom"));
        this.atomCLabel = new JLabel();
        mainPanel.add(this.atomCLabel);
        
        mainPanel.add(new JLabel("Second Acceptor"));
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
	
	public void setToDefaultValues() {
		// TODO : remove!
	}
	
	public void setHBondResidues(int donorResidueNumber, int acceptorResidueNumber) {
		this.atomALabel.setText(donorResidueNumber + ".N");
		this.atomBLabel.setText(donorResidueNumber + ".H");
		this.atomCLabel.setText(acceptorResidueNumber + ".O");
		this.atomDLabel.setText(acceptorResidueNumber + ".C");
	}
	
	public HBondMeasure getMeasure(int startResidueNumber, int endResidueNumber) {
		return descriptionFactory.createHBondMeasure(startResidueNumber, endResidueNumber);
	}
	
	public void actionPerformed(ActionEvent ae) {
		 String command = ae.getActionCommand();
	        if (command.equals("Update")) {
	        } else if (command.equals("Revert")) {
	        }
	}

}
