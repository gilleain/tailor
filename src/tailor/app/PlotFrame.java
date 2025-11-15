package tailor.app;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import tailor.category.Category;
import tailor.category.CategoryChangeListener;

public class PlotFrame extends JFrame implements ActionListener{
	
	private CategoryChangeListener categoryChangeListener;
	private RamachandranPlotPanel plotPanel;
	private CategoryListPanel categoryPanel;
	
	public PlotFrame(CategoryChangeListener categoryChangeListener) {
		this.categoryChangeListener = categoryChangeListener;
		
		JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton createCategoryButton = new JButton("Create Category");
        createCategoryButton.setActionCommand("Create Category");
        createCategoryButton.addActionListener(this);
        toolBar.add(createCategoryButton);
        
        JButton editCategoryButton = new JButton("Edit Category");
        editCategoryButton.setActionCommand("Edit Category");
        editCategoryButton.addActionListener(this);
        toolBar.add(editCategoryButton);
        
        JButton deleteCategoryButton = new JButton("Delete Category");
        deleteCategoryButton.setActionCommand("Delete Category");
        deleteCategoryButton.addActionListener(this);
        toolBar.add(deleteCategoryButton);
        
        JButton showCategoryOnPlotButton = new JButton("Show Selected");
        showCategoryOnPlotButton.setActionCommand("Show Category");
        showCategoryOnPlotButton.addActionListener(this);
        toolBar.add(showCategoryOnPlotButton);
        
        JButton clearCategoryOnPlotButton = new JButton("Clear Selected");
        clearCategoryOnPlotButton.setActionCommand("Clear Category");
        clearCategoryOnPlotButton.addActionListener(this);
        toolBar.add(clearCategoryOnPlotButton);
		
		this.categoryPanel = new CategoryListPanel();
		this.plotPanel = new RamachandranPlotPanel();
        
		JPanel categoryAndControlsPanel = new JPanel(new BorderLayout());
		categoryAndControlsPanel.add(toolBar, BorderLayout.NORTH);
		categoryAndControlsPanel.add(this.categoryPanel, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(categoryAndControlsPanel, BorderLayout.WEST);
		this.add(plotPanel, BorderLayout.EAST);
		this.pack();
	}
	
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		
		if (command.equals("Create Category")) {
			int n = this.plotPanel.getNumberOfResidues();
			CategoryDialog createDialog = new CategoryDialog(this, n);
			Category category = createDialog.getCategory();
			createDialog.dispose();
			if (category != null) {
				this.categoryChangeListener.setCategory(category);
				this.categoryPanel.addCategory(category);
			}
		} else if (command.equals("Clear Category")) {
			this.plotPanel.clearBounds();
			this.categoryChangeListener.clearSelection();
		} else if (command.equals("Edit Category")) {
			Category category = this.categoryPanel.getSelectedCategory();
			CategoryDialog editDialog = new CategoryDialog(this, category);
			Category newCategory = editDialog.getCategory();
			editDialog.dispose();
			if (newCategory != null) {
				this.categoryPanel.removeCategory(category);
				this.categoryPanel.addCategory(newCategory);
			}
		} else if (command.equals("Delete Category")) {
            this.categoryPanel.deleteSelected();
        } else if (command.equals("Show Category")) {
            Category selectedCategory = this.categoryPanel.getSelectedCategory();
            if (selectedCategory != null) {
                this.plotPanel.showCategory(selectedCategory);
                this.categoryChangeListener.selectInCategory(selectedCategory);
            }
        } else if (command.equals("Save Image")) {
            FileDialog fileDialog = new FileDialog(this, "Save Image To File", FileDialog.SAVE);
            fileDialog.setVisible(true);
            String directory = fileDialog.getDirectory();
            String filename = fileDialog.getFile();
            if (directory != null && filename != null) {
                Image image = this.plotPanel.getImage();
                File file = new File(directory, filename);
                try {
                    ImageIO.write((RenderedImage) image, "PNG", file);
                } catch (IOException ioe) {
                    System.err.println(ioe);
                }
            }
        } else if (command.equals("Clear Plot")) {
        	this.plotPanel.clearPoints();
        }
	}
}
