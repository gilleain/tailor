package tailor.api;

/**
 * The outcome of applying a measure to some atoms.
 */
public interface Measurement<T> {
	
	boolean apply(Condition<T> condition);

}
