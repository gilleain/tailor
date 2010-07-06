package tailor.datasource;

import java.util.ArrayList;

import tailor.measure.Measure;

/**
 * A results printer is the output sink for results from an Engine. 
 * 
 * @author maclean
 *
 */
public interface ResultsPrinter {
	
	public void printHeader(ArrayList<Measure> measures);
	
	public void printResult(Result result);
	
	public void signalNextStructure();

}
