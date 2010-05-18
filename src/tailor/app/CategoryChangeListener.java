package tailor.app;

public interface CategoryChangeListener {
	
	public void selectInCategory(Category selectedCategory);
	
	public void setCategory(Category category);
	
	public void clearSelection();

}
