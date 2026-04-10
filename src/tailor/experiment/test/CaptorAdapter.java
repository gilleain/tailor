package tailor.experiment.test;

import java.util.List;

import tailor.experiment.api.Source;
import tailor.experiment.operator.AbstractOperator;
import tailor.experiment.plan.Result;

public class CaptorAdapter extends AbstractOperator {
	
	private List<Result> capture;
	private Source<Result> input;
	
	public CaptorAdapter(Source<Result> input, List<Result> capture) {
		this.input = input;
		this.capture = capture;
	}

	@Override
	public void run() {
		while (input.hasNext()) {
			capture.add(input.getNext());
		}
	}

	@Override
	public String description() {
		return "Purely intended for tests";
	}

}
