package tailor.datasource;

import java.io.PrintStream;
import java.util.List;

import tailor.measurement.Measure;
import tailor.measurement.Measurement;

public class StreamResultsPrinter implements ResultsPrinter {
	
	private PrintStream out;
	private String columnSeparator;
	
	public StreamResultsPrinter(PrintStream out) {
		this.out = out;
		this.columnSeparator = "\t";
	}
	
	public void printHeader(List<Measure<? extends Measurement>> measures) {
		for (Measure<? extends Measurement> m : measures) {
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
