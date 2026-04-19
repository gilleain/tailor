package tailor.api;

/**
 * Output for items - next operation, printer, etc.
 */
public interface Sink<T> {
	
	void put(T item);
	
	/**
	 * @return the id of the sink operator
	 */
	String getSinkId();
	
	
	/**
	 * @param operator the operator that is the sink
	 */
	void registerSink(Operator operator);

}
