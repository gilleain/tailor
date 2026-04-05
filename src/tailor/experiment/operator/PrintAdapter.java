package tailor.experiment.operator;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Source;
import tailor.experiment.plan.Result;

/**
 * Convenience class for converting a pipe to output (feels like we should not need this...)
 */
public class PrintAdapter implements Operator {
	
	private Source<Result> output;
	
	public PrintAdapter(Source<Result> output) {
		this.output = output;
	}

	@Override
	public void run() {
		while (output.hasNext()) {
			System.out.println(output.getNext());
		}
	}

}
