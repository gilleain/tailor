package tops.translation.experimental;

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

}
