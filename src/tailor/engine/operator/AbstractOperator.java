package tailor.engine.operator;

import tailor.api.Operator;

public abstract class AbstractOperator implements Operator {
	
	protected String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	protected Pipe source;
	
	protected Pipe sink;
	
	public void clear() {
		source.clear();
		sink.clear();
	}
	
	@Override
	public void setOutput(Pipe output) {
		this.sink = output;
		this.sink.registerSource(this);
	}

	public void setInput(Pipe source) {
		this.source = source;
		this.source.registerSink(this);
	}

	public Pipe getInput() {
		return this.source;
	}

	public Pipe getOutput() {
		return this.sink;
	}

}
