package tailor.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tailor.api.Torsion;
import tailor.description.DescriptionFactory;
import tailor.measurement.TorsionMeasure;

public class TorsionMeasurePropertySheet extends JPanel implements ActionListener {
	
	private JTextField nameField;
	
	private JLabel atomALabel;
	private JLabel atomBLabel;
	private JLabel atomCLabel;
	private JLabel atomDLabel;
	
	private DescriptionFactory descriptionFactory;
	
	public TorsionMeasurePropertySheet(DescriptionFactory descriptionFactory) {
	    this.descriptionFactory = descriptionFactory;
	    
		this.setLayout(new BorderLayout());
		this.add(new JLabel("Torsion Measure", JLabel.CENTER), BorderLayout.NORTH);

		JPanel mainPanel = new JPanel(new GridLayout(5, 2));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        mainPanel.add(new JLabel("Name:"));
        
        this.nameField = new JTextField(4);
        mainPanel.add(this.nameField);
        
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
	}
	
	public void setToUnknownValues(Torsion torsion) {
	    if (torsion == Torsion.PHI) {
			this.nameField.setText("phi(i)");
			this.atomALabel.setText("i - 1.C");
			this.atomBLabel.setText("i.N");
			this.atomCLabel.setText("i.CA");
			this.atomDLabel.setText("i.C");
	    } else if (torsion == Torsion.PSI) {
			this.nameField.setText("psi(i)");
			this.atomALabel.setText("i.N");
			this.atomBLabel.setText("i.CA");
			this.atomCLabel.setText("i.C");
			this.atomDLabel.setText("i + 1.N");
	    } else if (torsion == Torsion.OMEGA) {
			this.nameField.setText("omega(i/i + 1)");
			this.atomALabel.setText("i.CA");
			this.atomBLabel.setText("i.C");
			this.atomCLabel.setText("i + 1.N");
			this.atomDLabel.setText("i + 1.CA");
		}
	}
	
	public void setValues(Torsion torsion, int startResidueNumber, int endResidueNumber) {
	    if (torsion == Torsion.PHI) {
			this.nameField.setText("phi(" + endResidueNumber + ")");
			this.atomALabel.setText(startResidueNumber + ".C");
			this.atomBLabel.setText(endResidueNumber + ".N");
			this.atomCLabel.setText(endResidueNumber + ".CA");
			this.atomDLabel.setText(endResidueNumber + ".C");
	    } else if (torsion == Torsion.PSI) {
			this.nameField.setText("psi(" + startResidueNumber + ")");
			this.atomALabel.setText(startResidueNumber + ".N");
			this.atomBLabel.setText(startResidueNumber + ".CA");
			this.atomCLabel.setText(startResidueNumber + ".C");
			this.atomDLabel.setText(endResidueNumber + ".N");
		}  else if (torsion == Torsion.OMEGA) {
			this.nameField.setText("omega(" + startResidueNumber + "/" + endResidueNumber + ")");
			this.atomALabel.setText(startResidueNumber + ".CA");
			this.atomBLabel.setText(startResidueNumber + ".C");
			this.atomCLabel.setText(endResidueNumber + ".N");
			this.atomDLabel.setText(endResidueNumber + ".CA");
		}
	}
	
	public TorsionMeasure getMeasure(int residueNumber) {
		String torsionName = this.nameField.getText();
		if (torsionName.startsWith("phi")) {
		    return descriptionFactory.createPhiMeasure(torsionName, residueNumber, "A");
		} else if (torsionName.startsWith("psi")) {
		    return descriptionFactory.createPsiMeasure(torsionName, residueNumber, "A");
		} else {
		    return null;
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
