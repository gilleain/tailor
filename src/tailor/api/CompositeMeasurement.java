package tailor.api;

public interface CompositeMeasurement<T> extends Measurement<T> {
	
	boolean apply(String partLabel, Condition<T> condition);

}
