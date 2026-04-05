package tailor.experiment.api;

/**
 * Operators perform a step in the pipeline, such as scanning for properties, combining inputs, or filtering the results.
 */
public interface Operator {
	
	void run();

	String description();
	
	String getId();
	
	void setId(String id);
	
}
