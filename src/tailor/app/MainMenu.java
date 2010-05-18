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

//		JMenuItem openTextDataItem = new JMenuItem("Open Text Data...");
//		openTextDataItem.setActionCommand("Open Text Data");
//		openTextDataItem.addActionListener(listener);
//		fileMenu.add(openTextDataItem);

//		JMenuItem openXMLDataItem = new JMenuItem("Open MSD Data...");
//		openXMLDataItem.setActionCommand("Open MSD Data");
//		openXMLDataItem.addActionListener(listener);
//		fileMenu.add(openXMLDataItem);
//
//		JMenuItem openXMLQueryItem = new JMenuItem("Open XML Query...");
//		openXMLQueryItem.setActionCommand("Open XML Query");
//		openXMLQueryItem.addActionListener(listener);
//		fileMenu.add(openXMLQueryItem);
		
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


//		JMenuItem editCategoryItem = new JMenuItem("Edit Category...");
//		editCategoryItem.setActionCommand("Edit Category");
//		editCategoryItem.addActionListener(listener);
//		editMenu.add(editCategoryItem);

		this.add(editMenu);

		JMenu runMenu = new JMenu("Run");

		this.runItem = new JMenuItem("Run");
		this.runItem.setActionCommand("Run");
		runItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_R, KeyEvent.META_MASK));
		this.runItem.addActionListener(listener);
		runMenu.add(this.runItem);

//		JMenuItem runFileSystemItem = new JMenuItem("Run File Query...");
//		runFileSystemItem.setActionCommand("Run File Query");
//		runFileSystemItem.addActionListener(listener);
//		runMenu.add(runFileSystemItem);
//
//		JMenuItem runMSDItem = new JMenuItem("Run MSD Query...");
//		runMSDItem.setActionCommand("Run MSD Query");
//		runMSDItem.addActionListener(listener);
//		runMenu.add(runMSDItem);

		this.add(runMenu);

		JMenu plotMenu = new JMenu("Plot");

//		JMenuItem openPlotItem = new JMenuItem("Open Plot Window...");
//		openPlotItem.setActionCommand("Open Plot Window");
//		openPlotItem.addActionListener(listener);
//		plotMenu.add(openPlotItem);
//
//	    JMenuItem addColumnsItem = new JMenuItem("Add Columns To Plot");
//	    addColumnsItem.setActionCommand("Add Columns To Plot");
//	    addColumnsItem.addActionListener(listener);
//	    plotMenu.add(addColumnsItem);
//	
	    JMenuItem saveImageItem = new JMenuItem("Save Plot Image");
	    saveImageItem.setActionCommand("Save Plot Image");
	    saveImageItem.addActionListener(listener);
	    plotMenu.add(saveImageItem);
	
//	    JMenuItem clearPlotItem = new JMenuItem("Clear Plot");
//	    clearPlotItem.setActionCommand("Clear Plot");
//	    clearPlotItem.addActionListener(listener);
//	    plotMenu.add(clearPlotItem);

		this.add(plotMenu);
	}
	
	public void setRunItemEnabled(boolean isEnabled) {
		this.runItem.setEnabled(isEnabled);
	}

}
