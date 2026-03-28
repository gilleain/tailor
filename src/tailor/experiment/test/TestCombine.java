package tailor.experiment.test;

import java.util.List;

import org.junit.Test;

import tailor.experiment.operator.AtomPipe;
import tailor.experiment.operator.CombineAtoms;
import tailor.experiment.operator.GroupPipe;
import tailor.experiment.operator.PrintAtomLists;
import tailor.experiment.operator.ScanAtomByLabel;
import tailor.structure.Chain;

public class TestCombine {
	
	
	/**
	 * - Scan for 'O' atoms and 'N' atoms
	 * - Combine these into (O, N) pairs
	 */ 
	@Test
	public void testCombine() {
		PrintAtomLists sink = new PrintAtomLists();
		Chain chain = Helper.makeData();
		GroupPipe inPipe = new GroupPipe(chain);
		
		AtomPipe oPipe = new AtomPipe();
		ScanAtomByLabel scanO = new ScanAtomByLabel("O", inPipe, oPipe);
		
		AtomPipe nPipe = new AtomPipe();
		ScanAtomByLabel scanN = new ScanAtomByLabel("N", inPipe, nPipe);
		
		CombineAtoms combineON = new CombineAtoms(List.of(oPipe, nPipe), sink);
		
		// run the pipeline
		Helper.runAll(List.of(scanO, scanN, combineON));
	}
	
}
