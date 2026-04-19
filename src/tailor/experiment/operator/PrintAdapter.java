package tailor.experiment.operator;

import tailor.api.Source;
import tailor.experiment.plan.Result;

/**
 * Convenience class for converting a pipe to output (feels like we should not need this...)
 */
public class PrintAdapter extends AbstractOperator {
	
	private Source<Result> input;
	
	public PrintAdapter(String id, ResultPipe input) {
		this.id = id;
		this.input = input;
	}
	
	public void setId(String id) {
		super.setId(id);
		((ResultPipe)this.input).registerSink(this);// TODO
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

}
