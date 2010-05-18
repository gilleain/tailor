package tailor.msdmotif;

import java.io.PrintStream;

public class MSDMotifStreamResultsPrinter implements MSDMotifResultsPrinter {
	
	private PrintStream out;
	
	public MSDMotifStreamResultsPrinter(PrintStream out) {
		this.out = out;
	}
	
	public void printResult(MSDMotifResult result) {
		out.print(result.toString());
	}

}
