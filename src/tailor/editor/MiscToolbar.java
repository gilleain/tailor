package tailor.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class MiscToolbar extends JToolBar implements ActionListener {

	private ResidueDiagramEditor editor;
	private JToggleButton scaleUpButton;
	private JToggleButton scaleDownButton;

	public MiscToolbar(ResidueDiagramEditor editor, ButtonGroup buttonGroup) {
		this.editor = editor;

		this.setFloatable(false);
		
		// this was a test, but the code might come in useful.
//		JToggleButton drawLabelButton = new JToggleButton("L");
//		drawLabelButton.setActionCommand("TOGGLE LABELS");
//		drawLabelButton.addActionListener(this);
//		buttonGroup.add(drawLabelButton);
//		this.add(drawLabelButton);
		
		this.scaleUpButton = new JToggleButton("+");
		this.scaleUpButton.setActionCommand("SCALE UP");
		this.scaleUpButton.addActionListener(this);
		buttonGroup.add(this.scaleUpButton);
		this.add(this.scaleUpButton);
		
		this.scaleDownButton = new JToggleButton("-");
		this.scaleDownButton.setActionCommand("SCALE DOWN");
		this.scaleDownButton.addActionListener(this);
		buttonGroup.add(this.scaleDownButton);
		this.add(this.scaleDownButton);
		
		JToggleButton addResidueButton = new JToggleButton("Add Residue");
		addResidueButton.setActionCommand("ADD RESIDUE");
		addResidueButton.addActionListener(this);
		buttonGroup.add(addResidueButton);
		this.add(addResidueButton);
		
		JToggleButton removeResidueButton = new JToggleButton("Remove Residue");
		removeResidueButton.setActionCommand("REMOVE RESIDUE");
		removeResidueButton.addActionListener(this);
		buttonGroup.add(removeResidueButton);
		this.add(removeResidueButton);

		this.scaleUpButton.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("ADD RESIDUE")) {
			editor.addResidueToEnd();
		} else if (command.equals("REMOVE RESIDUE")) {
			editor.removeResidueFromEnd();
		} else if (command.equals("SCALE UP")) {
			editor.scaleUp();
		} else if (command.equals("SCALE DOWN")) {
			editor.scaleDown();
		} else if (command.equals("TOGGLE LABELS")) {
			editor.toggleLabels();
		}
	}
	
	public void setScaleUpButtonEnabled(boolean isEnabled) {
		this.scaleUpButton.setEnabled(isEnabled);
	}
	
	public void setScaleDownButtonEnabled(boolean isEnabled) {
		this.scaleDownButton.setEnabled(isEnabled);
	}
}


