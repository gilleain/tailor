package tailor.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import tailor.description.Description;
import tailor.description.ProteinDescription;
import tailor.editor.DescriptionTreeView;
import tailor.editor.SelectionDialog;
import tailor.engine.Run;
import tailor.measure.AngleMeasure;
import tailor.measure.DistanceMeasure;
import tailor.measure.Measure;
import tailor.measure.TorsionMeasure;


public class RunDialog extends JDialog implements ActionListener, TreeSelectionListener {
    
    private DescriptionTreeView treeView;
    
    private JList measureList;
    
    private JTextField directoryField; 
    
    private JList filenameList;
    
    private Run run;
    
    private boolean isOkay;
    
    public RunDialog(Frame parent, Description description) {
        super(parent, "Create Run", true);
        this.setLayout(new BorderLayout());
        
        JPanel labelPanel = new JPanel(new GridLayout(1, 3));
        labelPanel.add(new JLabel("Motif", JLabel.CENTER));
        labelPanel.add(new JLabel("Files", JLabel.CENTER));
        labelPanel.add(new JLabel("Measures", JLabel.CENTER));
        this.add(labelPanel, BorderLayout.NORTH);
        
        this.treeView = new DescriptionTreeView(description);
        this.treeView.addTreeSelectionListener(this);
        this.add(this.treeView, BorderLayout.WEST);
        
        JPanel measurePanel = new JPanel(new BorderLayout());
        JPanel measureControls = new JPanel(new GridLayout(1, 2));
        
        JButton addMeasureButton = new JButton("Add");
        addMeasureButton.addActionListener(this);
        measureControls.add(addMeasureButton);
        
        JButton removeMeasureButton = new JButton("Remove");
        removeMeasureButton.addActionListener(this);
        measureControls.add(removeMeasureButton);
        
        this.measureList = new JList(new DefaultListModel());
        this.measureList.setPreferredSize(new Dimension(150, 200));
        
        measurePanel.add(measureControls, BorderLayout.NORTH);
        measurePanel.add(this.measureList, BorderLayout.CENTER);
        this.add(measurePanel, BorderLayout.EAST);
        
        JPanel filePanel = new JPanel(new BorderLayout());
        // TODO : make this relative to current directory
        this.directoryField = new JTextField("/Users/maclean/development/projects/tailor/structures");
        this.directoryField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Directory"),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        this.filenameList = new JList();
        this.filenameList.setPreferredSize(new Dimension(150, 250));
        this.filenameList.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Files"),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        
        filePanel.add(this.directoryField, BorderLayout.NORTH);
        filePanel.add(this.filenameList, BorderLayout.SOUTH);
        filePanel.setPreferredSize(new Dimension(150, 300));
        
        this.add(filePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        
        JButton createButton = new JButton("Create");
        createButton.setActionCommand("Create");
        createButton.addActionListener(this);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
        
        this.pack();
        this.setLocation(200, 200);
        
        this.run = new Run();
        this.run.setDescription((ProteinDescription) description);
        
        this.setIsOkay(true);
    }
    
    public boolean isOkay() {
        return this.isOkay;
    }
    
    public Run getRun() {
        // get the measures from the list and add to the Run
        DefaultListModel model = (DefaultListModel) this.measureList.getModel();
        for (int i = 0; i < model.size(); i++) {
            Measure measure = (Measure) model.get(i);
            this.run.getDescription().addMeasure(measure);
        }
        
        // get the directory
        String directory = this.directoryField.getText();
        try {
            this.run.setPath(new File(directory));
        } catch (IOException ioe) {
            // FIXME
            System.err.println(ioe);
        }
        
        return this.run;
    }
    
    private void setIsOkay(boolean isOkay) {
        System.err.println("setting isokay to " + isOkay);
        this.isOkay = isOkay;
    }
    
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        if (command.equals("Create")) {
            this.setIsOkay(true);
            
            this.setVisible(false);
        } else if (command.equals("Cancel")) {
            this.setIsOkay(false);
            this.setVisible(false);
        } else if (command.equals("Add")) {
            Description description = this.treeView.getDescription(0);
            SelectionDialog selectionDialog = 
                new SelectionDialog(description, this, "Create Measure");
            selectionDialog.setVisible(true);
            if (selectionDialog.isComplete()) {
                ArrayList<Description> paths = selectionDialog.getDescriptions();
                String type = selectionDialog.getCurrentlySelectedType();
                Measure measure;
                if (type.equals("Distance")) {
                    measure = new DistanceMeasure(paths.get(0), paths.get(1));
                } else if (type.equals("Angle")) {
                    measure = new AngleMeasure(paths.get(0), paths.get(1), paths.get(2));
                } else if (type.equals("Torsion")) {
                    measure = new TorsionMeasure(paths.get(0), paths.get(1), paths.get(2), paths.get(3));
                } else {
                    return; // just in case
                }
                ((DefaultListModel) this.measureList.getModel()).addElement(measure);
            }
            
        } else if (command.equals("Remove")) {
            
        }
    }
    
    public void valueChanged(TreeSelectionEvent tse) {
        
    }

}
