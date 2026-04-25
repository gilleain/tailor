package tailor.engine;

import java.util.List;

import tailor.api.AtomListMeasure;
import tailor.api.ResultsPrinter;
import tailor.engine.plan.Result;

public class SysoutResultsPrinter implements ResultsPrinter {

	@Override
	public void printHeader(List<AtomListMeasure> measures) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printResult(Result result) {
		System.out.println(result);
	}

	@Override
	public void signalNextStructure() {
		// TODO Auto-generated method stub
		
	}

}
