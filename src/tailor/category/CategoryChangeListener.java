package tailor.category;

public interface CategoryChangeListener {
	
	public void selectInCategory(Category selectedCategory);
	
	public void setCategory(Category category);
	
	public void clearSelection();

}
