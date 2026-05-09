package tops.translation.experimental;

public abstract class AbstractOperator implements Operator {
	
	private String id;
	
	private Pipe output;

	public abstract void run();
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setOutput(Pipe output) {
		this.output = output;
		output.registerSource(this);
	}
	
	public Pipe getOutput() {
		return this.output;
	}

}
