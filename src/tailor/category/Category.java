package tailor.category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tailor.category.filter.Bound;
import tailor.category.filter.Filter;

/**
 * A Category is like a motif Description, except that it is less precise. It is
 * intended to be used as a filter for the measurement lists from motif searches.
 * A filter is some bound or constraint on one or more columns of the result table.
 * 
 * @author maclean
 *
 */
public class Category {

	private String name;
	private List<Filter> filters;
	private List<String> memberIdList;
	
	/**
	 * @param name an arbitrary name for this category
	 */
	public Category(String name) {
		this.name = name;
		this.filters = new ArrayList<>();
		this.memberIdList = new ArrayList<>();
	}
    
    public String getName() {
        return this.name;
    }
    
    public List<Filter> getFilters() {
        return this.filters;
    }
    
    public List<Bound> getBounds() {
        List<Bound> bounds = new ArrayList<>();
        for (Filter filter : this.filters) {
            if (filter instanceof Bound) {
                bounds.add((Bound) filter);
            }
        }
        return bounds;
    }
    
    public Iterator<Filter> iterator() {
        return this.filters.iterator();
    }
    
    public Filter getFilter(int i) {
        return this.filters.get(i);
    }
    
    public int getNumberOfFilters() {
        return this.filters.size();
    }
	
	public void addFilter(Filter filter) {
		this.filters.add(filter);
	}
    
    public void addBound(Bound bound) {
        this.filters.add(bound);
    }
	
	public boolean accept(double[] values) {
	    for (Filter filter : this.filters) {
			if (filter.accept(values)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
 	
	public void addId(String id) {
		this.memberIdList.add(id);
	}
    
    public List<String> getMemberIds() {
        return this.memberIdList;
    }
    
    public int getNumberOfMembers() {
        return this.memberIdList.size();
    }
	
	public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Filter filter : this.filters) {
            stringBuffer.append(filter).append(" ");
        }
		return this.name + " " + this.getNumberOfMembers() + " " + stringBuffer;
	}
	
}
