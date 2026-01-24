package tailor.category.filter;

/**
 * Filter a row of values (TODO - better name?)
 */
public interface RowFilter {
	
	public boolean accept(double[] values);	// TODO - accept a 'Row' object?
}
