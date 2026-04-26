package tailor.api;

/**
 * @param <T>
 */
public interface Source<T> {
	
	void clear();
	
	/**
	 * The size of the T that is returned.
	 * TODO - why do we need this?
	 */
	int getArity();
	
	/**
	 * Get the next item from the source. 
	 *
	 */
	T getNext();
	
	/**
	 * @return true if there is another item
	 */
	boolean hasNext();
	
	
	/**
	 * @return the id of the source operator
	 */
	String getSourceId();
	
	
	/**
	 * @param operator the operator that is the source
	 */
	void registerSource(Operator operator);

}
