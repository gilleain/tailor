package tailor.datasource;

import java.io.PrintStream;
import java.util.List;

import tailor.measure.Measure;

public class StreamResultsPrinter implements ResultsPrinter {
	
	private PrintStream out;
	private String columnSeparator;
	
	public StreamResultsPrinter(PrintStream out) {
		this.out = out;
		this.columnSeparator = "\t";
	}
	
	public void printHeader(List<Measure> measures) {
		for (Measure m : measures) {
			this.out.print(m);
			this.out.print(this.columnSeparator);
		}
		this.out.println();
	}

	public void printResult(Result result) {
		this.out.println(result.toString());
	}

	public void signalNextStructure() {
		// nothing for now
	}

}
