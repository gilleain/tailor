package tailor.app;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import tailor.category.Category;
import tailor.category.filter.Bound;
import tailor.category.filter.BoundShape;

/**
 * @author maclean
 *
 */
public class CategoryDialog extends JDialog implements ActionListener {
    
    private JLabel[] residueNameLabels;
    private JComboBox<String>[] boundTypeComboBoxes;
    
    private TorsionDial[] torsionX;
    private TorsionDial[] torsionY;
    
    private JTextField[] rotationFields;
    
    private JLabel nameLabel;
    private JTextField nameField;
    
    private JButton confirmButton;
    private JButton cancelButton;
    
    private int numberOfResidues;
    private Category category;
    
    private static final String[] boundTypes = { "Square", "Round" };
    
    public CategoryDialog(JFrame parent, int numberOfResidues) {
        super(parent, "Create Category", true);
        
        System.err.println("creating category");
        
        this.category = null;
        this.numberOfResidues = numberOfResidues;
        this.createInterface(null);
    }
    
    public CategoryDialog(JFrame parent, Category category) {
        super(parent, "Edit Category", true);
        
        System.err.println("editing category " + category);
        
        this.numberOfResidues = category.getNumberOfFilters() / 2;
        this.createInterface(category);
        this.category = category;
        
        this.setFieldsFromCategory(category);
    }
    
    @SuppressWarnings("unchecked")
    public void createInterface(Category category) {
        JPanel namePanel = new JPanel();
        this.nameLabel = new JLabel("Name");
        namePanel.add(this.nameLabel);
        
        this.nameField = new JTextField(10);
        namePanel.add(this.nameField);
        this.add(namePanel, BorderLayout.NORTH);
        
        JPanel controlPanel = new JPanel(new GridLayout(this.numberOfResidues + 1, 5));
        
        controlPanel.add(new JLabel("Residue"));
        controlPanel.add(new JLabel("\u03C6"));
        controlPanel.add(new JLabel("\u03C8"));
        controlPanel.add(new JLabel("Bound Type"));
        controlPanel.add(new JLabel("Rotation"));
        
        this.residueNameLabels = new JLabel[this.numberOfResidues];

        this.torsionX = new TorsionDial[this.numberOfResidues];
        this.torsionY = new TorsionDial[this.numberOfResidues];
        
        this.boundTypeComboBoxes = new JComboBox[this.numberOfResidues];
        this.rotationFields = new JTextField[this.numberOfResidues];
        
        for (int i = 0; i < this.numberOfResidues; i++) {
            this.residueNameLabels[i] = new JLabel(String.valueOf(i + 1), JLabel.CENTER);
            controlPanel.add(this.residueNameLabels[i]);
            
            this.torsionX[i] = new TorsionDial(150, 150);
            controlPanel.add(this.torsionX[i]);
            
            this.torsionY[i] = new TorsionDial(150, 150);
            controlPanel.add(this.torsionY[i]);
            
            this.boundTypeComboBoxes[i] = new JComboBox<>(CategoryDialog.boundTypes);
            controlPanel.add(boundTypeComboBoxes[i]);
            
            this.rotationFields[i] = new JTextField("0");
            controlPanel.add(this.rotationFields[i]);
        }
        this.add(controlPanel, BorderLayout.CENTER);
 
        JPanel buttonPanel = new JPanel();
        
        this.confirmButton = new JButton("Okay");
        this.confirmButton.setActionCommand("Okay");
        this.confirmButton.addActionListener(this);
        buttonPanel.add(this.confirmButton);
        
        this.cancelButton = new JButton("Cancel");
        this.cancelButton.setActionCommand("Cancel");
        this.cancelButton.addActionListener(this);
        buttonPanel.add(this.cancelButton);
        
        this.add(buttonPanel, BorderLayout.SOUTH);
        
        if (category != null) {
            this.setFieldsFromCategory(category);
        }
        
        this.pack();
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setVisible(true);
    }
    
    public void setFieldsFromCategory(Category category) {
        System.err.println("setting values from " + category);
        this.nameField.setText(category.getName());
        for (int i = 0; i < this.numberOfResidues; i++) {
            Bound bound = (Bound) category.getFilter(i);
            if (Bound.shapeType == BoundShape.OVAL) {
                this.boundTypeComboBoxes[i].setSelectedItem(CategoryDialog.boundTypes[1]);
            }
 
            this.torsionX[i].setMin(bound.getMinX());
            this.torsionX[i].setMax(bound.getMaxX());
            this.torsionY[i].setMin(bound.getMinY());
            this.torsionY[i].setMax(bound.getMaxY());
        }
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        if (command.equals("Okay")) {
            this.category = new Category(this.nameField.getText());
            for (int i = 0; i < this.numberOfResidues; i++) {
                int columnIndex = i * 2;
                
                double xMin = this.torsionX[i].getMin();
                double xMax = this.torsionX[i].getMax();
                
                double yMin = this.torsionY[i].getMin();
                double yMax = this.torsionY[i].getMax();
                
                System.err.println("Got " + xMin + ", " + xMax + ", " + yMin + ", " + yMax);
                
                double theta = Double.valueOf(this.rotationFields[i].getText());
                
                BoundShape boundShape;
                if (this.boundTypeComboBoxes[i].getSelectedItem().equals("Square")) {
                    boundShape = BoundShape.RECTANGULAR;
                } else {
                    boundShape = BoundShape.OVAL;
                }
                
                Bound bound = new Bound(columnIndex, columnIndex + 1, xMin, xMax, yMin, yMax, boundShape);
                
                if (theta != 0.0) {
                    bound.rotateAboutCenter(theta);
                }
                
                this.category.addBound(bound);
            }
            this.setVisible(false);
        } else if (command.equals("Cancel")) {
            this.setVisible(false);
        }
    }

}
