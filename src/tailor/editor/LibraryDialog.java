package tailor.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tailor.description.Description;

public class LibraryDialog extends JDialog implements ActionListener, ListSelectionListener {
	
	private JLabel directoryLabel;
	
	private JList<Description> descriptionList;
	
	private JButton confirmButton;
	
	private boolean isOkay;

	public LibraryDialog(Frame parent) {
		this(parent, null);
	}
	
	public LibraryDialog(Frame parent, String directory) {
		super(parent, "Select a motif", true);
		
		// set up the descriptions
		this.directoryLabel = new JLabel();
		this.directoryLabel.setHorizontalAlignment(JLabel.CENTER);
		
		ArrayList<Description> descriptions;
		if (directory != null) {
			descriptions = new DescriptionLibrary(directory).getDescriptions();
			this.directoryLabel.setText(directory);
		} else {
			// TODO : throw an exception if resources not found?
			descriptions = DescriptionLibrary.getDefaultLibrary();
			this.directoryLabel.setText("Default");
		}
		this.add(this.directoryLabel, BorderLayout.NORTH);
		
		// create the list
		final int w = 250;
		final int h = 100;
		
		descriptionList = new JList<Description>(descriptions.toArray(new Description[] {}));
		descriptionList.setCellRenderer(new DiagramListCellRenderer(w, h, descriptions));
		descriptionList.addListSelectionListener(this);
		
		JScrollPane scroll = new JScrollPane(descriptionList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setPreferredSize(new Dimension(300, 500));
		descriptionPanel.add(scroll);
		this.add(descriptionPanel, BorderLayout.CENTER);
		
		// okay and cancel buttons
		JPanel buttonPanel = new JPanel();
		
		this.confirmButton = new JButton("Okay");
		this.confirmButton.setActionCommand("Okay");
		this.confirmButton.setEnabled(false);
		this.confirmButton.addActionListener(this);
		buttonPanel.add(this.confirmButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);

		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setLocation(300, 100);
		this.pack();
		
		this.isOkay = false;
	}
	
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if (command.equals("Okay")) {
			this.isOkay = true;
			this.setVisible(false);
		} else if (command.equals("Cancel")) {
			this.isOkay = false;
			this.setVisible(false);
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		// we don't particularly care which value is selected...
		this.confirmButton.setEnabled(true);
	}
	
	public boolean isOkay() {
		return this.isOkay;
	}
	
	public Description getDescription() {
		return (Description) this.descriptionList.getSelectedValue();
	}
	
	public static void main(String[] args) {
		String directory = null;
		if (args.length == 0) { directory = "descriptions"; }
		Frame f = new Frame();
		LibraryDialog dialog = new LibraryDialog(f, directory);
		dialog.setLocation(700, 10);
		dialog.setVisible(true);
		f.dispose();
	}
	
}
