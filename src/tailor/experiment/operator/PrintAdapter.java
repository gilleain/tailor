package tailor.experiment.operator;

import tailor.experiment.api.Source;
import tailor.experiment.plan.Result;

/**
 * Convenience class for converting a pipe to output (feels like we should not need this...)
 */
public class PrintAdapter extends AbstractOperator {
	
	private Source<Result> input;
	
	public PrintAdapter(String id, ResultPipe input) {
		this.id = id;
		this.input = input;
		input.registerSink(this);
	}

	@Override
	public void run() {
		while (input.hasNext()) {
			System.out.println(input.getNext());
		}
	}

	@Override
	public String description() {
		return "Print id[" + getId() + "]";
	}

}
