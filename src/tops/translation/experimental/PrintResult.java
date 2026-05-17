package tops.translation.experimental;

import tailor.engine.operator.AbstractOperator;
import tailor.engine.operator.Pipe;

public class PrintResult extends AbstractOperator {
	
	private Pipe input;
	
	public PrintResult(Pipe input) {
		this.input = input;
	}

	@Override
	public void run() {
		while (input.hasNext()) {
			System.out.println(input.getNext());
		}
	}

	@Override
	public String description() {
		return "Print";
	}

}
