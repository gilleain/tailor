package tailor.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class ConditionToolbar extends JToolBar implements ActionListener {
	
	private ResidueDiagramEditor editor;
	
	public ConditionToolbar(ResidueDiagramEditor editor, ButtonGroup buttonGroup) {
		this.editor = editor;
		
		this.setFloatable(false);
		
		JToggleButton addPhiTorsionButton = new JToggleButton("Add Phi Condition");
		addPhiTorsionButton.setActionCommand("ADD PHI TORSION");
		addPhiTorsionButton.addActionListener(this);
		buttonGroup.add(addPhiTorsionButton);
		this.add(addPhiTorsionButton);
		
		JToggleButton addPsiTorsionButton = new JToggleButton("Add Psi Condition");
		addPsiTorsionButton.setActionCommand("ADD PSI TORSION");
		addPsiTorsionButton.addActionListener(this);
		buttonGroup.add(addPsiTorsionButton);
		this.add(addPsiTorsionButton);

		JToggleButton addArcButton = new JToggleButton("Add HBond Condition");
		addArcButton.setActionCommand("ADD HBOND");
		addArcButton.addActionListener(this);
		buttonGroup.add(addArcButton);
		this.add(addArcButton);
		
		addPhiTorsionButton.setSelected(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("ADD PHI TORSION")) {
			editor.startMakingPhiTorsionCondition();
		} else if (command.equals("ADD PSI TORSION")) {
			editor.startMakingPsiTorsionCondition();
        } else if (command.equals("ADD HBOND")) {
        	editor.startMakingHBondCondition();
        }
	}
}
