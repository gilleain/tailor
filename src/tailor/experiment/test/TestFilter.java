package tailor.experiment.test;

import static tailor.experiment.test.Helper.pathTo;

import java.util.List;

import org.junit.Test;

import tailor.condition.atom.AtomAngleCondition;
import tailor.condition.atom.AtomDistanceCondition;
import tailor.engine.operator.CombineResults;
import tailor.engine.operator.FilterAtomResultByCondition;
import tailor.engine.operator.GroupSource;
import tailor.engine.operator.PrintAdapter;
import tailor.engine.operator.PrintResults;
import tailor.engine.operator.ResultPipe;
import tailor.engine.operator.ScanAtomResultByLabel;
import tailor.experiment.description.GroupDescription;
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
		
		GroupDescription groupA = Helper.makeGroupDescription("O");
		GroupDescription groupB = Helper.makeGroupDescription("N");
		AtomDistanceCondition condition = new AtomDistanceCondition(
				distance, List.of(Helper.pathTo(groupA, "O"), Helper.pathTo(groupB, "N")));
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(List.of(condition));
		filter.setSource(onPipe);
		ResultPipe end = new ResultPipe();
		filter.setSink(end);
		
		PrintAdapter printAdapter = new PrintAdapter("END", end);
		
		// run the pipeline
		Helper.runAll(List.of(groupSource, scanO, scanN, combineON, filter, printAdapter));
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
		
		GroupDescription group = Helper.makeGroupDescription("C", "CA", "N");
		AtomAngleCondition condition = 
				new AtomAngleCondition(angle, pathTo(group, "N"), pathTo(group, "CA"), pathTo(group, "C"));
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(List.of(condition));
		filter.setSource(triplePipe);
		filter.setSink(new PrintResults());
		Helper.runAll(List.of(groupSource, scanTriple, filter));
	}
	
}
