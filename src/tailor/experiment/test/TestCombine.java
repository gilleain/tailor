package tailor.experiment.test;

import java.util.List;

import org.junit.Test;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.operator.AtomListPipe;
import tailor.experiment.operator.AtomPipe;
import tailor.experiment.operator.AtomResultPipe;
import tailor.experiment.operator.CombineAtomLists;
import tailor.experiment.operator.CombineAtoms;
import tailor.experiment.operator.CombineResults;
import tailor.experiment.operator.GroupPipe;
import tailor.experiment.operator.PrintAtomLists;
import tailor.experiment.operator.PrintResults;
import tailor.experiment.operator.ScanAtomByLabel;
import tailor.experiment.operator.ScanAtomListsByLabel;
import tailor.experiment.operator.ScanAtomResultByLabel;
import tailor.structure.Atom;
import tailor.structure.Chain;

public class TestCombine {
	
	@Test
	public void testCombineResults() {
		PrintResults sink = new PrintResults();
		Chain chain = Helper.makeData();
		GroupPipe inPipe1 = new GroupPipe(chain);	// TODO - annoying - otherwise need to reset iterator
		GroupPipe inPipe2 = new GroupPipe(chain);
		
		AtomResultPipe oPipe = new AtomResultPipe();
		ScanAtomResultByLabel scanO = new ScanAtomResultByLabel("O");
		scanO.setSource(inPipe1);
		scanO.setSink(oPipe);
		
		AtomResultPipe nPipe = new AtomResultPipe();
		ScanAtomResultByLabel scanN = new ScanAtomResultByLabel("N");
		scanN.setSource(inPipe2);
		scanN.setSink(nPipe);
		
		CombineResults combineON = new CombineResults(List.of(oPipe, nPipe), sink);
		
		// run the pipeline
		Helper.runAll(List.of(scanO, scanN, combineON));
	}
	
	
	/**
	 * - Scan for 'O' atoms and 'N' atoms
	 * - Combine these into (O, N) pairs
	 */ 
	@Test
	public void testCombineAtoms() {
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
	
	
	/**
	 * - Scan for 'N' and 'CA' atom pairs
	 * - Scan for 'C' and 'O' atom pairs
	 * - Combine these pairs into quads
	 */
	@Test
	public void testCombineAtomLists() {
		Chain chain = Helper.makeData();
		GroupPipe inPipe1 = new GroupPipe(chain);	// TODO - annoying - otherwise need to reset iterator
		GroupPipe inPipe2 = new GroupPipe(chain);
		
		AtomListPipe outPipe1 = new AtomListPipe();
		ScanAtomListsByLabel scan1 = new ScanAtomListsByLabel(List.of("N", "CA"), inPipe1, outPipe1);
		
		AtomListPipe outPipe2 = new AtomListPipe();
		ScanAtomListsByLabel scan2 = new ScanAtomListsByLabel(List.of("C", "O"), inPipe2, outPipe2);
		
		List<Source<List<Atom>>> sources = List.of(outPipe1, outPipe2);
		CombineAtomLists combine12 = new CombineAtomLists(sources, new Sink<List<List<Atom>>>() {
			// TODO - clearly a more general type needed
			public void put(List<List<Atom>> item) {
				System.out.println(item);
			}
		});
		Helper.runAll(List.of(scan1, scan2, combine12));
	}
	
}
