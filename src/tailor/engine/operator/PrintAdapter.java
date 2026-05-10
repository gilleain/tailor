package tailor.engine.operator;

/**
 * Convenience class for converting a pipe to output (feels like we should not need this...)
 */
public class PrintAdapter extends AbstractOperator {
	
	private Pipe input;
	
	public PrintAdapter(Pipe input) {
		this("", input);
	}
	
	public PrintAdapter(String id, Pipe input) {
		this.id = id;
		this.input = input;
	}
	
	public void setId(String id) {
		super.setId(id);
		this.input.registerSink(this);
	}
	

	@Override
	public void run() {
		int count = 1;
		while (input.hasNext()) {
			System.out.println(count + "\t" + input.getNext());
			count++;
		}
	}

	@Override
	public String description() {
		return "Print id[" + getId() + "]";
	}

	@Override
	public void clear() {
		
	}

}
