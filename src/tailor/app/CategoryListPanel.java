package tailor.app;

import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * Display and manage a list of Category objects.
 * 
 * @author maclean
 *
 */
public class CategoryListPanel extends JPanel {
    
    private JList<Category> categoryDisplay;
    
    public CategoryListPanel() {
        this.categoryDisplay = new JList<Category>(new DefaultListModel<Category>());
        this.categoryDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //this.categoryDisplay.setPreferredSize(new Dimension(200, 400));
        JScrollPane scrollPane = new JScrollPane(this.categoryDisplay);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        
        this.add(scrollPane);
    }
    
    public void addCategory(Category category) {
        this.getListModel().addElement(category);
//        this.categoryDisplay.setSelectedValue(category, true);
    }
    
    public void removeCategory(Category category) {
        this.getListModel().removeElement(category);
    }
    
    public DefaultListModel<Category> getListModel() {
        return (DefaultListModel<Category>) this.categoryDisplay.getModel();
    }
    
    public void deleteSelected() {
        Object selected = this.categoryDisplay.getSelectedValue();
        this.getListModel().removeElement(selected);
    }
    
    public Category getSelectedCategory() {
        return (Category) this.categoryDisplay.getSelectedValue();
    }

}
