package tailor.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import tailor.datasource.GuiResultsPrinter;
import tailor.datasource.StructureSource;
import tailor.datasource.XmlDescriptionReader;
import tailor.datasource.XmlDescriptionWriter;
import tailor.description.Description;
import tailor.description.ProteinDescription;
import tailor.editor.LibraryDialog;
import tailor.editor.MeasureListBox;
import tailor.engine.BasicEngine;
import tailor.engine.Engine;
import tailor.engine.Run;

public class RunFrame extends JFrame implements ActionListener, CategoryChangeListener {

	private MainMenu menu;
	
	private DescriptionPanel descriptionPanel;
	
	private MeasureListBox measureList;
	
	private JLabel currentRunDirectory;
	
	private ResultTable resultTable;
	
	private MultipleAnalysisPanel multipleAnalysisPanel;
	
	private boolean hasDescription;
	
	private String libraryDirectory;
	
	private Engine engine;
	
	public RunFrame() {
		super("");
		
		this.menu = new MainMenu(this);
		this.add(this.menu, BorderLayout.NORTH);
		
		Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(1, 1, 1, 1), 
                BorderFactory.createLineBorder(Color.GRAY));
		
		MouseListener mouseListener = new MouseAdapter() {
		     public void mouseClicked(MouseEvent e) {
		         if (e.getClickCount() == 2) {
		             System.out.println("Double clicked on Description Panel");
		             if (descriptionPanel.hasDescription()) {
		            	 fireEditEvent();
		             }
		          }
		     }
		 };
		this.descriptionPanel = new DescriptionPanel(mouseListener);
		
		JPanel leftHandPanel = new JPanel(new BorderLayout());
		leftHandPanel.add(this.descriptionPanel, BorderLayout.NORTH);
		
		this.measureList = new MeasureListBox();
		leftHandPanel.add(this.measureList, BorderLayout.CENTER);
		
		Border dirBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(1, 1, 1, 1),
                BorderFactory.createTitledBorder(
                		BorderFactory.createLineBorder(Color.GRAY),
                "Current Run Directory"));
		
		File dir = new File(this.getWorkingDirectory(), "structures");
		this.currentRunDirectory = new JLabel(dir.toString());
		this.currentRunDirectory.setBorder(dirBorder);
		leftHandPanel.add(this.currentRunDirectory, BorderLayout.SOUTH);
		
		this.add(leftHandPanel, BorderLayout.WEST);
		 
        this.resultTable = new ResultTable();
        this.resultTable.setBorder(border);
     
        this.add(this.resultTable, BorderLayout.CENTER);

        this.multipleAnalysisPanel = new MultipleAnalysisPanel(this.resultTable);
        this.add(this.multipleAnalysisPanel, BorderLayout.EAST);
        
        this.hasDescription = false;
        this.menu.setRunItemEnabled(false);
//        this.engine = null;
        
        this.libraryDirectory = null;
       
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocation(20, 100);
        this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        
        if (command.equals("New Description")) {
        	EditorDialog descriptionEditor = new EditorDialog(this);
        	
        	if (descriptionEditor.isOkay()) {
	        	ProteinDescription newDescription = descriptionEditor.getDescription();
	        	System.out.println("New " + newDescription.toPathString());
	        	this.setDescription(newDescription);
	        	this.measureList.setMeasures(descriptionEditor.getMeasures());
        	} else {
        		System.out.println("New Motif Cancelled");
        	}
        } else if (command.equals("Open Description")) {
        	LibraryDialog libDialog;
        	if (this.libraryDirectory == null) {
        		libDialog = new LibraryDialog(this);
        	} else {
        		libDialog = new LibraryDialog(this, this.libraryDirectory);
        	}
        	libDialog.setVisible(true);
        	if (libDialog.isOkay()) {
        		Description description = libDialog.getDescription();
        		this.setDescription(description);
        	}
        } else if (command.equals("Edit Description")) {
        	Description description = this.descriptionPanel.getDescription();
        	Description edited = this.editDescription(description);
        	if (!description.equals(edited)) {
        		this.descriptionPanel.setDescription(edited);
        	}
        } else if (command.equals("Save Description")) {
        	File file = this.chooseFile(false, "Save Description To Xml File");
        	try {
        		Description description =  this.descriptionPanel.getDescription();
        		XmlDescriptionWriter.writeToFile(file, description);
        	} catch (IOException ioe) {
        	}
        } else if (command.equals("Save Results")) {
            File saveFile = this.chooseFile(false, "Save Results To File");
            if (saveFile != null) {
            	this.writeTextFile(saveFile);
            }
        } else if (command.equals("Select Working Directory")) {
        	this.chooseWorkingDirectory();
        } else if (command.equals("Run")) {
            this.doRun();
        } else if (command.equals("Close")) {
            this.close();
        } else if (command.equals("Quit")) {
            this.quit();
        }  else if (command.equals("Save Plot Image")) {
        	File file = this.chooseFile(false, "Create Plot Image File");
        	if (file != null) {
        		Image image = this.multipleAnalysisPanel.getPlotImage();
        		try {
        			ImageIO.write((RenderedImage) image, "PNG", file);
        		} catch (IOException ioe) {
        			System.err.println(ioe);
        		}
        	}
        }
        	
	}
	
	public File chooseFile(boolean fileExists, String title) {
		 
        JFileChooser fileChooser = new JFileChooser(this.getWorkingDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setDialogTitle(title);
        
        int returnValue;
        if (fileExists) {
            returnValue = fileChooser.showOpenDialog(this);
        } else {
            returnValue = fileChooser.showSaveDialog(this);
        }
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }
    
	public String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }
	
	public void chooseWorkingDirectory() {
		JFileChooser fileChooser = new JFileChooser(this.getWorkingDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	File selectedFile = fileChooser.getSelectedFile();
        	System.out.println(selectedFile);
        	if (selectedFile != null) {
        		this.currentRunDirectory.setText(selectedFile.toString());
        	}
        }
	}
	
	public boolean canRun() {
		return this.hasDescription;
	}
	
	public void doRun() {
		if (this.hasDescription) {
			try {
				Run run = new Run((ProteinDescription)this.descriptionPanel.getDescription(),
						this.measureList.getMeasures(),
						this.currentRunDirectory.getText());
			
				int m = run.getMeasures().size();
				FileDataTableModel fileDataTableModel = new FileDataTableModel(m + 2);
				this.resultTable.setModel(fileDataTableModel);

				StructureSource source = run.getStructureSource();
				
				// this has to be final to be accessible from the anonymous Thread below
				final ProgressDialog progressDialog = new ProgressDialog(source.size());

				progressDialog.setVisible(true);
				
				// TODO : don't want to be making a new engine every time!
				this.engine = new BasicEngine(
						new GuiResultsPrinter(fileDataTableModel, 
								progressDialog.getProgressBar()), 
								System.err,
								source
				);
//				this.engine.run(run);
				
				// FIXME Ack! - this should be done with SwingWorker?
				((BasicEngine)this.engine).setRun(run);
				Thread engineThread = new Thread() {
					public void run() {
						((BasicEngine)engine).run();
						progressDialog.setVisible(false);
						resultTable.setTitle();
					}
				};
				engineThread.start();
				
			} catch (IOException ioe) {
				return;		// FIXME
			}
		} else {
			// shouldn't happen, as run button should be disabled, but could throw an error 
		}
	}
	
	public void close() {
		this.resultTable.clearData();
		this.descriptionPanel.clear();
		this.measureList.clear();
		this.multipleAnalysisPanel.clear();
		this.hasDescription = false;
		this.menu.setRunItemEnabled(false);
	}
	
	public void quit() {
        System.exit(0);
    }
	
	public void loadMotifFile(File file) throws IOException {
		XmlDescriptionReader reader = new XmlDescriptionReader();
		Description description = reader.readDescription(file);
		this.setDescription(description); 
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
	
	private void setDescription(Description description) {
		this.descriptionPanel.setDescription(description);
        this.hasDescription = true;
        this.menu.setRunItemEnabled(true);
	}
	
	public void fireEditEvent() {
		Description original = this.descriptionPanel.getDescription();
		Description edited = this.editDescription(original);
		this.descriptionPanel.setEdited(edited);
	}

	public Description editDescription(Description description) {
		
		// make a copy
		Description copyOfDescription = 
			new ProteinDescription((ProteinDescription)description);	// XXX
		
		// edit the copy
		EditorDialog descriptionEditor = new EditorDialog(this, copyOfDescription);

		// the "okay" button was pressed
		if (descriptionEditor.isOkay()) {
			// save the measures
        	this.measureList.setMeasures(descriptionEditor.getMeasures()); 
			
			// return the (edited) copy
			return descriptionEditor.getDescription();
		} else {
			// return the original
			return description;
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
	
	public void setLibraryDirectory(String directoryPath) {
		this.libraryDirectory = directoryPath;
	}
	
	public void processCommandLineArguments(String[] args) {
        String f = args[0];
        if (f.equals("--xml-motif") || f.equals("-x")) {
            try {
                this.loadMotifFile(new File(args[1]));
            } catch (IOException ioe) {
                System.err.println(ioe);
            }
        } else if (f.equals("--library-directory") || f.equals("-l")) {
        	this.setLibraryDirectory(args[1]);
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        RunFrame runFrame = new RunFrame();
        if (args.length > 0) {
            runFrame.processCommandLineArguments(args);
        }
    }


}
