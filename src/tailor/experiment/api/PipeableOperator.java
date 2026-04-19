package tailor.experiment.api;


/**
 * Operator that can have a source and sink.
 * 
 * @param <I> the type of items in the input source
 * @param <O> the type of items in the output sink
 */
public interface PipeableOperator<I, O> extends Operator {
	
	void setSource(Source<I> source);
	
	Source<I> getSource();
	
	void setSink(Sink<O> sink);
	
	Sink<I> getSink();
	
}
