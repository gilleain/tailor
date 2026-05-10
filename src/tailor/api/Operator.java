package tailor.api;

import tailor.engine.operator.Pipe;

/**
 * Operators perform a step in the pipeline, such as scanning for properties, combining inputs, or filtering the results.
 */
public interface Operator {
	
	void run();

	String description();
	
	String getId();
	
	void setId(String id);
	
	void clear();
	
	void setOutput(Pipe output);
	
	void setInput(Pipe input);
	
	Pipe getOutput();
	
	Pipe getInput();
	
}
