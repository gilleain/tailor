package tailor.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import tailor.datasource.GuiResultsPrinter;
import tailor.datasource.xml.XmlDescriptionReader;
import tailor.description.Description;
import tailor.description.ProteinDescription;
import tailor.engine.Engine;
import tailor.engine.EngineFactory;
import tailor.engine.Run;
import tailor.msdmotif.DescriptionToXmlQueryTranslator;
import tailor.msdmotif.MSDMotifResultTableModel;


/**
 * @author maclean
 *
 */
public class App implements ActionListener, CategoryChangeListener {
    
    private JFrame frame;
    
    private DescriptionList descriptionList;
    
    private JList<Run> runList;
    
    private ResultTable resultTable;
    
    private JmolPanel jMolPanel;
    
    private Engine engine;
    
    public App() {
        this.frame = new JFrame("Tailor");
        
        // make the menu bar
        MainMenu menuBar = new MainMenu(this);
        this.frame.add(menuBar, BorderLayout.NORTH);
        
        // add the other components
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(1, 1, 1, 1), 
                BorderFactory.createLineBorder(Color.GRAY));
        JPanel motifAndRunPanel = new JPanel(new GridLayout(2, 1));
        motifAndRunPanel.setPreferredSize(new Dimension(100, 600));
        
        this.descriptionList = new DescriptionList(border, this);
        motifAndRunPanel.add(this.descriptionList, BorderLayout.NORTH);
        
        this.runList = new JList<Run>(new DefaultListModel<Run>());
        this.runList.setPreferredSize(new Dimension(100, 300));
        motifAndRunPanel.add(new JScrollPane(this.runList), BorderLayout.CENTER);
        
        this.frame.add(motifAndRunPanel, BorderLayout.WEST);
        
        this.resultTable = new ResultTable();
        this.resultTable.setBorder(border);
     
        this.frame.add(this.resultTable, BorderLayout.CENTER);
        this.jMolPanel = new JmolPanel(this.resultTable);
        this.frame.add(this.jMolPanel, BorderLayout.EAST);
        
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.pack();
        this.frame.setLocation(100, 100);
        this.frame.setVisible(true);
        
        this.engine = null;
    }

    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        
        if (command.equals("New Description")) {
        	EditorDialog descriptionEditor = new EditorDialog(this.frame);
        	
        	if (descriptionEditor.isOkay()) {
	        	ProteinDescription newDescription = descriptionEditor.getDescription();
	        	System.out.println("New " + newDescription.toPathString());
	        	this.descriptionList.add(newDescription);
        	} else {
        		System.out.println("New Motif Cancelled");
        	}
        } else if (command.equals("Edit Description")) {
        	Description description = this.descriptionList.getSelectedDescription();
        	Description edited = this.editDescription(description);
        	if (!description.equals(edited)) {
        		this.descriptionList.replace(description, edited);
        	}
        } else if (command.equals("Open Description")) {
            File file = this.chooseFile(true, true);
            if (file != null) {
                try {
                    this.loadMotifFile(file);
                } catch (IOException ioe) {
                    // TODO : notify user of this error
                    System.err.println(ioe);
                }
            }
        } else if (command.equals("Open Plot Window")) {
        	PlotFrame plotFrame = new PlotFrame(this);
        	plotFrame.setVisible(true);
        } else if (command.equals("Add Columns To Plot")) {
        	//FIXME : move to plotframe?
//            ArrayList<String> idColumn = this.resultTable.getIdColumn(); 
//            this.plotPanel.addColumns(idColumn, this.resultTable.getSelectedColumns());
        } else if (command.equals("Select Columns")) {
            // TODO : FIX THIS!
            this.resultTable.selectColumns(2, 7);
        } else if (command.equals("Open Text Data")) {
            File textFile = this.chooseFile(false, true);
            if (textFile != null) {
                this.readFromTextFile(textFile);
            }
        } else if (command.equals("Open XML Query")) {
            File file = this.chooseFile(true, true);
            if (file != null) {
                
            }
        } else if (command.equals("Open MSD Data")) {
            File file = this.chooseFile(true, true);
            if (file != null) {
                MSDMotifResultTableModel model = new MSDMotifResultTableModel();
//                this.msdDataSource.processResult(file, model);	FIXME
                this.resultTable.setModel(model);
            }
        } else if (command.equals("Save Results")) {
            File saveFile = this.chooseFile(false, false);
            this.writeTextFile(saveFile);
        } else if (command.equals("Create Run")){
            this.createRun();
        } else if (command.equals("Run MSD Query")) {
            this.runMSDQuery();
        } else if (command.equals("Run File Query")) {
            this.runFileQuery();
        } else if (command.equals("Close")) {
            this.close();
        } else if (command.equals("Quit")) {
            this.quit();
        }
    }
    
    public void setCategory(Category category) {
    	this.resultTable.assignToCategory(category);
		this.resultTable.selectRowsInCategory(category);
    }
    
    public void selectInCategory(Category category) {
    	this.resultTable.selectRowsInCategory(category);
    }
    
    public void clearSelection() {
    	this.resultTable.clearSelection();
    }
    
    public String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    public File chooseFile(boolean useXmlExtension, boolean fileExists) {
 
        JFileChooser fileChooser = new JFileChooser(this.getWorkingDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        int returnValue;
        if (fileExists) {
            returnValue = fileChooser.showOpenDialog(this.frame);
        } else {
            returnValue = fileChooser.showSaveDialog(this.frame);
        }
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }
    
    public void close() {
        this.resultTable.clearData();
//        this.plotPanel.clearPoints();		// FIXME : close the plot window?
        ((DefaultListModel<Run>)this.runList.getModel()).clear();
        this.descriptionList.clear();
        this.jMolPanel.clear();
    }
    
    public void quit() {
        System.exit(0);
    }
    
    public void runMSDQuery() {
        System.out.println("run msd!");
        Description[] descriptions = this.descriptionList.getAllDescriptions();
        
        for (Description description : descriptions) {
            String xml = DescriptionToXmlQueryTranslator.translate(description);
            System.out.println(xml);
        }
        
    }
    
    private void addRunToList(Run run) {
    	((DefaultListModel<Run>) this.runList.getModel()).addElement(run);
    }
    
    public void createRun() {
    	Description description = this.descriptionList.getSelectedDescription();
    	
        RunDialog runDialog = new RunDialog(this.frame, description);
        runDialog.setVisible(true);
        
        System.err.println("Dialog" + runDialog.isOkay());
        
        if (runDialog.isOkay()) {
        	System.err.println("getting run");
            Run run = runDialog.getRun();
            this.addRunToList(run);
            
            int m = run.getDescription().getMeasures().size();
        	FileDataTableModel fileDataTableModel = new FileDataTableModel(m + 2);
        	
        	// TODO : don't want to be making a new engine every time!
        	this.engine = EngineFactory.getEngine(
        	        description,
        			new GuiResultsPrinter(fileDataTableModel), 
        			System.err,
        			run.getStructureSource()
        	);
            
            this.resultTable.setModel(fileDataTableModel);
            this.engine.run(run);
        }
    }
    
    public void runFileQuery() {
        System.out.println("run files!");
        // TODO!
    }
    
    public void readFromTextFile(File file) {
        try {
            this.resultTable.setModel(new SimpleTableModel(file));
        } catch (IOException ioe) {
        	this.handleError(ioe.toString());
        }
    }
    
    public void writeTextFile(File file) {
        try {
            this.resultTable.writeToFile(file);
        } catch (IOException ioe) {
            this.handleError(ioe.toString());
        }
    }
    
    private void handleError(String message) {
    	System.err.println(message);
    	// TODO
    }
    
    public Description editDescription(Description description) {
    	// make a copy
    	Description copyOfDescription = 
    		new ProteinDescription((ProteinDescription)description);	// XXX
    	
    	// edit the copy
    	EditorDialog descriptionEditor = new EditorDialog(this.frame, copyOfDescription);
    	
    	// the "okay" button was pressed
    	if (descriptionEditor.isOkay()) {
    		// return the (edited) copy
    		return descriptionEditor.getDescription();
    	} else {
    		// return the original
    		return description;
    	}
    }
    
    public void loadMotifFile(File file) throws IOException {
        XmlDescriptionReader reader = new XmlDescriptionReader();
        Description description = reader.readDescription(file);
        this.descriptionList.add(description);
    }
    
    public void processCommandLineArguments(String[] args) {
        String fileTypeFlag = args[0];
        if (fileTypeFlag.equals("--xml-motif")) {
            try {
                this.loadMotifFile(new File(args[1]));
            } catch (IOException ioe) {
                System.err.println(ioe);
            }
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        App tailorApp = new App();
        if (args.length > 0) {
            tailorApp.processCommandLineArguments(args);
        }
    }

}
