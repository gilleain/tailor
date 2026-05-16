package tailor.operator;

import static tailor.operator.Helper.pathTo;

import java.util.List;

import org.junit.Test;

import tailor.description.GroupDescription;
import tailor.description.atom.AtomAngleDescription;
import tailor.description.atom.AtomDistanceDescription;
import tailor.engine.operator.CombineResults;
import tailor.engine.operator.FilterAtomResultByCondition;
import tailor.engine.operator.GroupSource;
import tailor.engine.operator.Pipe;
import tailor.engine.operator.PrintAdapter;
import tailor.engine.operator.ScanAtomResultByLabel;
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
		Pipe resultPipe1 = new Pipe();
		Pipe resultPipe2 = new Pipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1, resultPipe2));
		
		Pipe oPipe = new Pipe();
		ScanAtomResultByLabel scanO = new ScanAtomResultByLabel(List.of("O"));
		scanO.setOutput(oPipe);
		scanO.setInput(resultPipe1);
		
		Pipe nPipe = new Pipe();
		ScanAtomResultByLabel scanN = new ScanAtomResultByLabel(List.of("N"));
		scanN.setOutput(nPipe);
		scanN.setInput(resultPipe2);
		
		Pipe onPipe = new Pipe();
		CombineResults combineON = new CombineResults(List.of(oPipe, nPipe), onPipe);
		
		GroupDescription groupA = Helper.makeGroupDescription("O");
		GroupDescription groupB = Helper.makeGroupDescription("N");
		AtomDistanceDescription condition = new AtomDistanceDescription(
				distance, Helper.pathTo(groupA, "O"), Helper.pathTo(groupB, "N"));
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(List.of(condition));
		filter.setInput(onPipe);
		Pipe end = new Pipe();
		filter.setOutput(end);
		
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
		Pipe groupResultPipe = new Pipe();
		GroupSource groupSource = new GroupSource(chain, List.of(groupResultPipe));
		Pipe triplePipe = new Pipe();
		ScanAtomResultByLabel scanTriple = new ScanAtomResultByLabel(List.of("N", "CA", "C"));
		scanTriple.setInput(groupResultPipe);
		scanTriple.setOutput(triplePipe);
		
		GroupDescription group = Helper.makeGroupDescription("C", "CA", "N");
		AtomAngleDescription condition = 
				new AtomAngleDescription(angle, pathTo(group, "N"), pathTo(group, "CA"), pathTo(group, "C"));
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(List.of(condition));
		filter.setInput(triplePipe);
		Pipe end = new Pipe();
		filter.setOutput(end);
		Helper.runAll(List.of(groupSource, scanTriple, filter, new PrintAdapter(end)));
	}
	
}
