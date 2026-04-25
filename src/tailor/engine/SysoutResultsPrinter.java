package tailor.engine;

import java.util.List;
import java.util.stream.Collectors;

import tailor.api.AtomListMeasure;
import tailor.api.ResultsPrinter;
import tailor.engine.plan.Result;

public class SysoutResultsPrinter implements ResultsPrinter {
	
	private int counter = 0;

	@Override
	public void printHeader(List<AtomListMeasure> measures) {
		counter = 0;
		System.out.println("Index\tHit\t" + 
				measures.stream().map(AtomListMeasure::getName).collect(Collectors.joining("\t"))
		);
	}

	@Override
	public void printResult(Result result) {
		System.out.println(counter + "\t" + result);
		counter++;
	}

	@Override
	public void signalNextStructure() {
		// Do nothing, no status bar
		
	}

}
