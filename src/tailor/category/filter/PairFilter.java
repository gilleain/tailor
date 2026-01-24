package tailor.category.filter;

/**
 * Filter a pair of values
 */
public interface PairFilter {
	
	public boolean accept(double firstValue, double secondValue);

}
