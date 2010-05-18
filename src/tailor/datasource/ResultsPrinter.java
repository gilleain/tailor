package tailor.datasource;

import java.util.ArrayList;

import tailor.measure.Measure;

public interface ResultsPrinter {
	
	public void printHeader(ArrayList<Measure> measures);
	
	public void printResult(Result result);
	
	public void signalNextStructure();

}
