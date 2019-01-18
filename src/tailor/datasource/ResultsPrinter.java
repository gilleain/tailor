package tailor.datasource;

import java.util.List;

import tailor.measurement.Measure;
import tailor.measurement.Measurement;

/**
 * A results printer is the output sink for results from an Engine. 
 * 
 * @author maclean
 *
 */
public interface ResultsPrinter {
	
	public void printHeader(List<Measure<? extends Measurement>> measures);
	
	public void printResult(Result result);
	
	public void signalNextStructure();

}
