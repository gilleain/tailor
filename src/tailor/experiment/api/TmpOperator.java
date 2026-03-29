package tailor.experiment.api;

// TODO - merge this extension with parent
public interface TmpOperator<I, O> extends Operator {
	
	void setSource(Source<I> source);
	
	void setSink(Sink<O> sink);

}
