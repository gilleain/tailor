package tailor.experiment.operator;

import tailor.api.PipeableOperator;
import tailor.api.Sink;
import tailor.api.Source;
import tailor.experiment.plan.Result;

public abstract class AbstractPipeableOperator extends AbstractOperator implements PipeableOperator<Result, Result> {

	protected Source<Result> source;
	
	protected Sink<Result> sink;

	@Override
	public void setSource(Source<Result> source) {
		this.source = source;
		this.getSourceAsSink().registerSink(this);
	}

	@Override
	public void setSink(Sink<Result> sink) {
		this.sink = sink;
		this.getSinkAsSource().registerSource(this);
	}

	@Override
	public Source<Result> getSource() {
		return this.source;
	}

	@Override
	public Sink<Result> getSink() {
		return this.sink;
	}
	
	private Source<Result> getSinkAsSource() {
		if (sink instanceof ResultPipe) {	// TODO - this seems odd
			return (Source<Result>)this.sink;
		}
		throw new IllegalStateException("Cannot cast sink as source");
	}
	
	private Sink<Result> getSourceAsSink() {
		if (source instanceof ResultPipe) {	// TODO - this seems odd
			return (Sink<Result>)this.source;
		}
		throw new IllegalStateException("Cannot cast source as sink");
	}
}
