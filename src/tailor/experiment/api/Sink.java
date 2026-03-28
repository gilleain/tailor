package tailor.experiment.api;

/**
 * Output for items - next operation, printer, etc.
 */
public interface Sink<T> {
	
	void put(T item);

}
