package tailor.app;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MainMenu extends JMenuBar {
	
	private JMenuItem runItem;
	
	public MainMenu(ActionListener listener) {
		JMenu fileMenu = new JMenu("File");

		JMenuItem newDescriptionItem = new JMenuItem("New Description...");
		newDescriptionItem.setActionCommand("New Description");
		newDescriptionItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, KeyEvent.META_MASK));
		newDescriptionItem.addActionListener(listener);
		fileMenu.add(newDescriptionItem);

		JMenuItem openDescriptionItem = new JMenuItem("Open Description...");
		openDescriptionItem.setActionCommand("Open Description");
		openDescriptionItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, KeyEvent.META_MASK));
		openDescriptionItem.addActionListener(listener);
		fileMenu.add(openDescriptionItem);

		// TODO JMenuItem openTextDataItem = new JMenuItem("Open Text Data...");

		// TODO	JMenuItem openXMLDataItem = new JMenuItem("Open MSD Data...");
		// TODO JMenuItem openXMLQueryItem = new JMenuItem("Open XML Query...");

		
		JMenuItem saveDescriptionItem = new JMenuItem("Save Description...");
		saveDescriptionItem.setActionCommand("Save Description");
		saveDescriptionItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, KeyEvent.META_MASK));
		saveDescriptionItem.addActionListener(listener);
		fileMenu.add(saveDescriptionItem);

		JMenuItem saveTextDataItem = new JMenuItem("Save Results...");
		saveTextDataItem.setActionCommand("Save Results");
		saveTextDataItem.addActionListener(listener);
		fileMenu.add(saveTextDataItem);

		JMenuItem closeItem = new JMenuItem("Close");
		closeItem.setActionCommand("Close");
		closeItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_W, KeyEvent.META_MASK));
		closeItem.addActionListener(listener);
		fileMenu.add(closeItem);

		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.setActionCommand("Quit");
		quitItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Q, KeyEvent.META_MASK));
		quitItem.addActionListener(listener);
		fileMenu.add(quitItem);

		this.add(fileMenu);

		JMenu editMenu = new JMenu("Edit");

		JMenuItem editDescriptionItem = new JMenuItem("Edit Description...");
		editDescriptionItem.setActionCommand("Edit Description");
		editDescriptionItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, KeyEvent.META_MASK));
		editDescriptionItem.addActionListener(listener);
		editMenu.add(editDescriptionItem);
		
		JMenuItem changeWorkingDirectoryItem = new JMenuItem("Select Working Directory...");
		changeWorkingDirectoryItem.setActionCommand("Select Working Directory");
		changeWorkingDirectoryItem.addActionListener(listener);
		editMenu.add(changeWorkingDirectoryItem);

		// TODO JMenuItem editCategoryItem = new JMenuItem("Edit Category...");

		this.add(editMenu);

		JMenu runMenu = new JMenu("Run");

		this.runItem = new JMenuItem("Run");
		this.runItem.setActionCommand("Run");
		runItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_R, KeyEvent.META_MASK));
		this.runItem.addActionListener(listener);
		runMenu.add(this.runItem);

		// TODO JMenuItem runFileSystemItem = new JMenuItem("Run File Query...");
		// TODO JMenuItem runMSDItem = new JMenuItem("Run MSD Query...");


		this.add(runMenu);

		JMenu plotMenu = new JMenu("Plot");

		// TODO JMenuItem openPlotItem = new JMenuItem("Open Plot Window...");
		// TODO JMenuItem addColumnsItem = new JMenuItem("Add Columns To Plot");
		
	    JMenuItem saveImageItem = new JMenuItem("Save Plot Image");
	    saveImageItem.setActionCommand("Save Plot Image");
	    saveImageItem.addActionListener(listener);
	    plotMenu.add(saveImageItem);
	
	    // TODO JMenuItem clearPlotItem = new JMenuItem("Clear Plot");


		this.add(plotMenu);
	}
	
	public void setRunItemEnabled(boolean isEnabled) {
		this.runItem.setEnabled(isEnabled);
	}

}
