package tailor.experiment.test;

import java.util.List;

import org.junit.Test;

import tailor.experiment.condition.AtomAngleCondition;
import tailor.experiment.condition.AtomDistanceCondition;
import tailor.experiment.condition.AtomPropertyCondition;
import tailor.experiment.operator.AtomListPipe;
import tailor.experiment.operator.AtomPipe;
import tailor.experiment.operator.ResultPipe;
import tailor.experiment.operator.CombineAtoms;
import tailor.experiment.operator.CombineResults;
import tailor.experiment.operator.FilterAtomListsByCondition;
import tailor.experiment.operator.FilterAtomsResultsByCondition;
import tailor.experiment.operator.GroupPipe;
import tailor.experiment.operator.GroupSource;
import tailor.experiment.operator.PrintAtomLists;
import tailor.experiment.operator.ScanAtomByLabel;
import tailor.experiment.operator.ScanAtomListsByLabel;
import tailor.experiment.operator.ScanAtomResultByLabel;
import tailor.structure.Chain;

public class TestFilter {
	
	@Test
	public void testFilterAtomCombinations_ByResult() {
		double distance = 5;	// whatever
		
		Chain chain = Helper.makeData();
		ResultPipe resultPipe = new ResultPipe();
		GroupSource groupSource = new GroupSource(chain, resultPipe);
		
		ResultPipe oPipe = new ResultPipe();
		ScanAtomResultByLabel scanO = new ScanAtomResultByLabel("O");
		scanO.setSink(oPipe);
		scanO.setSource(resultPipe);
		
		ResultPipe nPipe = new ResultPipe();
		ScanAtomResultByLabel scanN = new ScanAtomResultByLabel("O");
		scanN.setSink(nPipe);
		scanN.setSource(resultPipe);
		
		ResultPipe onPipe = new ResultPipe();
		CombineResults combineON = new CombineResults(List.of(oPipe, nPipe), onPipe);
		AtomPropertyCondition condition = new AtomPropertyCondition(null);	// TODO
//				new AtomDistanceCondition(distance);
		FilterAtomsResultsByCondition filter = new FilterAtomsResultsByCondition(condition);
		filter.setSource(onPipe);
		filter.setSink(onPipe);
		
		// run the pipeline
		Helper.runAll(List.of(groupSource, scanO, scanN, combineON, filter));
	}
	
	/**
	 * - Scan for 'O' atoms and 'N' atoms
	 * - Combine these into (O, N) pairs
	 * - Filter these pairs by distance
	 */
	@Test
	public void testFilterAtomCombinations() {
		double distance = 5;	// whatever
		
		Chain chain = Helper.makeData();
		GroupPipe inPipe = new GroupPipe(chain);
		
		AtomPipe oPipe = new AtomPipe();
		ScanAtomByLabel scanO = new ScanAtomByLabel("O", inPipe, oPipe);
		
		AtomPipe nPipe = new AtomPipe();
		ScanAtomByLabel scanN = new ScanAtomByLabel("N", inPipe, nPipe);
		
		AtomListPipe onPipe = new AtomListPipe();
		CombineAtoms combineON = new CombineAtoms(List.of(oPipe, nPipe), onPipe);
		FilterAtomListsByCondition filter = 
				new FilterAtomListsByCondition(new AtomDistanceCondition(distance), onPipe, new PrintAtomLists());
		
		// run the pipeline
		Helper.runAll(List.of(scanO, scanN, combineON, filter));
	}
	
	
	/**
	 * - Scan for {'C', 'CA', 'N'} atoms
	 * - Filter these by angle (TODO - alter input points to be filterable!)
	 */
	@Test
	public void testFilterAtomLists() {
		double angle = 90;
		
		Chain chain = Helper.makeData();
		GroupPipe inPipe = new GroupPipe(chain);
		AtomListPipe triplePipe = new AtomListPipe();
		ScanAtomListsByLabel scanTriple = new ScanAtomListsByLabel(List.of("C", "CA", "N"), inPipe, triplePipe);
		FilterAtomListsByCondition filter = 
				new FilterAtomListsByCondition(new AtomAngleCondition(angle), triplePipe, new PrintAtomLists());
		Helper.runAll(List.of(scanTriple, filter));
	}
	
}
