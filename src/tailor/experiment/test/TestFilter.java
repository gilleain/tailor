package tailor.experiment.test;

import java.util.List;

import org.junit.Test;

import tailor.experiment.condition.AtomAngleCondition;
import tailor.experiment.condition.AtomDistanceCondition;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.operator.CombineResults;
import tailor.experiment.operator.FilterAtomResultByCondition;
import tailor.experiment.operator.GroupSource;
import tailor.experiment.operator.PrintResults;
import tailor.experiment.operator.ResultPipe;
import tailor.experiment.operator.ScanAtomResultByLabel;
import tailor.structure.Chain;

public class TestFilter {
	
	/**
	 * - Scan for 'O' atoms and 'N' atoms
	 * - Combine these into (O, N) pairs
	 * - Filter these pairs by distance
	 */
	@Test
	public void testFilterAtomCombinations_ByResult() {
		double distance = 5;	// whatever
		
		Chain chain = Helper.makeData(3);
		ResultPipe resultPipe1 = new ResultPipe();
		ResultPipe resultPipe2 = new ResultPipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1, resultPipe2));
		
		ResultPipe oPipe = new ResultPipe();
		ScanAtomResultByLabel scanO = new ScanAtomResultByLabel(List.of("O"));
		scanO.setSink(oPipe);
		scanO.setSource(resultPipe1);
		
		ResultPipe nPipe = new ResultPipe();
		ScanAtomResultByLabel scanN = new ScanAtomResultByLabel(List.of("N"));
		scanN.setSink(nPipe);
		scanN.setSource(resultPipe2);
		
		ResultPipe onPipe = new ResultPipe();
		CombineResults combineON = new CombineResults(List.of(oPipe, nPipe), onPipe);
		
		AtomMatcher atomMatcher = new AtomMatcher(List.of("O", "N"));
		AtomDistanceCondition condition = new AtomDistanceCondition(distance);
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(condition, atomMatcher);
		filter.setSource(onPipe);
		filter.setSink(new PrintResults());
		
		// run the pipeline
		Helper.runAll(List.of(groupSource, scanO, scanN, combineON, filter));
	}
	
	/**
	 * - Scan for {'C', 'CA', 'N'} atoms
	 * - Filter these by angle (TODO - alter input points to be filterable!)
	 */
	@Test
	public void testFilterAtomLists() {
		double angle = 90;
		
		Chain chain = Helper.makeData(3);
		ResultPipe groupResultPipe = new ResultPipe();
		GroupSource groupSource = new GroupSource(chain, List.of(groupResultPipe));
		ResultPipe triplePipe = new ResultPipe();
		ScanAtomResultByLabel scanTriple = new ScanAtomResultByLabel(List.of("N", "CA", "C"));
		scanTriple.setSource(groupResultPipe);
		scanTriple.setSink(triplePipe);
		
		AtomMatcher atomMatcher = new AtomMatcher(List.of("C", "CA", "N"));
		AtomAngleCondition condition = new AtomAngleCondition(angle);
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(condition, atomMatcher);
		filter.setSource(triplePipe);
		filter.setSink(new PrintResults());
		Helper.runAll(List.of(groupSource, scanTriple, filter));
	}
	
}
