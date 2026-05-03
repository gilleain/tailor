package tailor.app;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MainMenu extends JMenuBar {
	
	public static final String NEW_DESCRIPTION = "New Description";
	public static final String OPEN_DESCRIPTION = "Open Description";
	public static final String SAVE_DESCRIPTION = "Save Description";
	public static final String SAVE_RESULTS = "Save Results";
	public static final String CLOSE = "Close";
	public static final String QUIT = "QUIT";
	public static final String EDIT_DESCRIPTION = "Edit Description";
	public static final String SELECT_WORKING_DESCRIPTION = "Select Working Directory";
	public static final String CREATE_RUN = "Creae Run";
	public static final String SAVE_PLOT_IMAGE = "Save Plot Image";
	public static final String OPEN_PLOT_WINDOW = "Open Plot Window";
	
	
	private JMenuItem runItem;
	
	public MainMenu(ActionListener listener) {
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem newDescriptionItem = new JMenuItem("New Description...");
		newDescriptionItem.setActionCommand(NEW_DESCRIPTION);
		newDescriptionItem.setAccelerator(makeMetaStroke(KeyEvent.VK_N));
		newDescriptionItem.addActionListener(listener);
		fileMenu.add(newDescriptionItem);

		JMenuItem openDescriptionItem = new JMenuItem("Open Description...");
		openDescriptionItem.setActionCommand(OPEN_DESCRIPTION);
		openDescriptionItem.setAccelerator(makeMetaStroke(KeyEvent.VK_O));
		openDescriptionItem.addActionListener(listener);
		fileMenu.add(openDescriptionItem);

		// TODO JMenuItem openTextDataItem = new JMenuItem("Open Text Data...");

		// TODO	JMenuItem openXMLDataItem = new JMenuItem("Open MSD Data...");
		// TODO JMenuItem openXMLQueryItem = new JMenuItem("Open XML Query...");

		
		JMenuItem saveDescriptionItem = new JMenuItem("Save Description...");
		saveDescriptionItem.setActionCommand(SAVE_DESCRIPTION);
		saveDescriptionItem.setAccelerator(makeMetaStroke(KeyEvent.VK_S));
		saveDescriptionItem.addActionListener(listener);
		fileMenu.add(saveDescriptionItem);

		JMenuItem saveTextDataItem = new JMenuItem("Save Results...");
		saveTextDataItem.setActionCommand(SAVE_RESULTS);
		saveTextDataItem.addActionListener(listener);
		fileMenu.add(saveTextDataItem);

		JMenuItem closeItem = new JMenuItem("Close");
		closeItem.setActionCommand(CLOSE);
		closeItem.setAccelerator(makeMetaStroke(KeyEvent.VK_W));
		closeItem.addActionListener(listener);
		fileMenu.add(closeItem);

		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.setActionCommand(QUIT);
		quitItem.setAccelerator(makeMetaStroke(KeyEvent.VK_Q));
		quitItem.addActionListener(listener);
		fileMenu.add(quitItem);

		this.add(fileMenu);

		JMenu editMenu = new JMenu("Edit");

		JMenuItem editDescriptionItem = new JMenuItem("Edit Description...");
		editDescriptionItem.setActionCommand(EDIT_DESCRIPTION);
		editDescriptionItem.setAccelerator(makeMetaStroke(KeyEvent.VK_E));
		editDescriptionItem.addActionListener(listener);
		editMenu.add(editDescriptionItem);
		
		JMenuItem changeWorkingDirectoryItem = new JMenuItem("Select Working Directory...");
		changeWorkingDirectoryItem.setActionCommand(SELECT_WORKING_DESCRIPTION);
		changeWorkingDirectoryItem.addActionListener(listener);
		editMenu.add(changeWorkingDirectoryItem);

		// TODO JMenuItem editCategoryItem = new JMenuItem("Edit Category...");

		this.add(editMenu);

		JMenu runMenu = new JMenu("Run");

		this.runItem = new JMenuItem("Run");
		this.runItem.setActionCommand(CREATE_RUN);
		runItem.setAccelerator(makeMetaStroke(KeyEvent.VK_R));
		this.runItem.addActionListener(listener);
		runMenu.add(this.runItem);

		// TODO JMenuItem runFileSystemItem = new JMenuItem("Run File Query...");
		// TODO JMenuItem runMSDItem = new JMenuItem("Run MSD Query...");

		this.add(runMenu);

		JMenu plotMenu = new JMenu("Plot");

		// TODO JMenuItem openPlotItem = new JMenuItem("Open Plot Window...");
		// openPlotItem.setActionCommand(OPEN_PLOT_WINDOW);
		
		// TODO JMenuItem addColumnsItem = new JMenuItem("Add Columns To Plot");
		
	    JMenuItem saveImageItem = new JMenuItem("Save Plot Image");
	    saveImageItem.setActionCommand(SAVE_PLOT_IMAGE);
	    saveImageItem.addActionListener(listener);
	    plotMenu.add(saveImageItem);
	
	    // TODO JMenuItem clearPlotItem = new JMenuItem("Clear Plot");

		this.add(plotMenu);
	}
	
	private KeyStroke makeMetaStroke(int keyEvent) {
		return KeyStroke.getKeyStroke(keyEvent, KeyEvent.META_DOWN_MASK);
	}
	
	public void setRunItemEnabled(boolean isEnabled) {
		this.runItem.setEnabled(isEnabled);
	}

}
