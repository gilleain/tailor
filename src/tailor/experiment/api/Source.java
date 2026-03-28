package tailor.experiment.api;

public interface Source<T> {
	
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

}
