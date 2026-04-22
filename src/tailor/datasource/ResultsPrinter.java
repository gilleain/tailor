package tailor.datasource;

import java.util.List;

import tailor.api.AtomListMeasure;
import tailor.engine.plan.Result;

/**
 * A results printer is the output sink for results from an Engine. 
 * 
 * @author maclean
 *
 */
public interface ResultsPrinter {
	
	public void printHeader(List<AtomListMeasure> measures);
	
	public void printResult(Result result);
	
	public void signalNextStructure();

}
