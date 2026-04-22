package tailor.app;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import tailor.api.AtomListMeasure;
import tailor.description.ChainDescription;
import tailor.editor.ResidueDiagramEditor;

public class EditorDialog extends JDialog implements ActionListener {
	
	private ResidueDiagramEditor editor;
	private boolean isOkay;
	
	public EditorDialog(JFrame parent, ChainDescription description) {
		super(parent, "Edit Description", true);
		
		this.setLayout(new BorderLayout());
		
		this.editor = new ResidueDiagramEditor(description);
		
		this.add(this.editor, BorderLayout.CENTER);
		this.createButtonPanel();
		
		this.pack();
		
		// center the dialog
		Point p = parent.getLocation();
		int newX = (p.x + (parent.getWidth() / 2)) - (this.getWidth() / 2);
		int newY = (p.y + (parent.getHeight() / 2)) - (this.getHeight() / 2);
		this.setLocation(newX, newY);
		
		this.setVisible(true);
	}
	
	public EditorDialog(JFrame parent) {
		super(parent, "Create Motif", true);
		
		this.setLayout(new BorderLayout());
		
		this.editor = new ResidueDiagramEditor();
		this.add(this.editor, BorderLayout.CENTER);
		this.createButtonPanel();
		
		this.pack();
		
		// center the dialog
		Point p = parent.getLocation();
		int newX = (p.x + (parent.getWidth() / 2)) - (this.getWidth() / 2);
		int newY = (p.y + (parent.getHeight() / 2)) - (this.getHeight() / 2);
		this.setLocation(newX, newY);
		
		this.setVisible(true);
	}
	
	private void createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		
		JButton confirmButton = new JButton("Okay");
		confirmButton.setActionCommand("Okay");
		confirmButton.addActionListener(this);
		buttonPanel.add(confirmButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public ChainDescription getDescription() {
		return this.editor.getDescription();
	}
	
	public List<AtomListMeasure> getMeasures() {
		return this.editor.getMeasures();
	}
	
	public boolean isOkay() {
		return this.isOkay;
	}
	
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if (command.equals("Okay")) {
			this.isOkay = true;
			this.setVisible(false);
		} else {
			this.isOkay = false;
			this.setVisible(false);
		}
	}

}
