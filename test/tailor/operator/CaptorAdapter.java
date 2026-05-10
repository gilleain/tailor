package tailor.operator;

import java.util.List;

import tailor.engine.operator.AbstractOperator;
import tailor.engine.operator.Pipe;
import tailor.engine.plan.Result;

public class CaptorAdapter extends AbstractOperator {
	
	private List<Result> capture;
	private Pipe input;
	
	public CaptorAdapter(Pipe input, List<Result> capture) {
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

	@Override
	public void clear() {
		
	}

}
