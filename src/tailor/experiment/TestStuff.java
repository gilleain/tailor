package tailor.experiment;

import java.util.List;

import org.junit.Test;

import tailor.experiment.api.Operator;
import tailor.experiment.condition.AtomAngleCondition;
import tailor.experiment.condition.AtomDistanceCondition;
import tailor.experiment.operator.AtomListPipe;
import tailor.experiment.operator.AtomPipe;
import tailor.experiment.operator.CombineAtoms;
import tailor.experiment.operator.FilterAtomListsByCondition;
import tailor.experiment.operator.GroupPipe;
import tailor.experiment.operator.PrintAtomLists;
import tailor.experiment.operator.PrintAtoms;
import tailor.experiment.operator.ScanAtomByLabel;
import tailor.experiment.operator.ScanAtomListsByLabel;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;

public class TestStuff {
	
	/**
	 * - Scan for 'O' atoms
	 */
	@Test
	public void testScanAtoms() {
		PrintAtoms sink = new PrintAtoms();
		Chain chain = makeData();
		ScanAtomByLabel scan = new ScanAtomByLabel("O", new GroupPipe(chain), sink);
		scan.run();
	}
	
	/**
	 * - Scan for {'C', 'CA', 'N'} atoms
	 */
	@Test
	public void testScanAtomLists() {
		PrintAtomLists sink = new PrintAtomLists();
		Chain chain = makeData();
		ScanAtomListsByLabel scan = new ScanAtomListsByLabel(List.of("C", "CA", "N"), new GroupPipe(chain), sink);
		scan.run();
	}
	
	/**
	 * - Scan for 'O' atoms and 'N' atoms
	 * - Combine these into (O, N) pairs
	 */ 
	@Test
	public void testCombine() {
		PrintAtomLists sink = new PrintAtomLists();
		Chain chain = makeData();
		GroupPipe inPipe = new GroupPipe(chain);
		
		AtomPipe oPipe = new AtomPipe();
		ScanAtomByLabel scanO = new ScanAtomByLabel("O", inPipe, oPipe);
		
		AtomPipe nPipe = new AtomPipe();
		ScanAtomByLabel scanN = new ScanAtomByLabel("N", inPipe, nPipe);
		
		CombineAtoms combineON = new CombineAtoms(List.of(oPipe, nPipe), sink);
		
		// run the pipeline
		runAll(List.of(scanO, scanN, combineON));
	}
	
	/**
	 * - Scan for 'O' atoms and 'N' atoms
	 * - Combine these into (O, N) pairs
	 * - Filter these pairs by distance
	 */
	@Test
	public void testFilterAtomCombinations() {
		double distance = 5;	// whatever
		
		Chain chain = makeData();
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
		runAll(List.of(scanO, scanN, combineON, filter));
	}
	
	
	/**
	 * - Scan for {'C', 'CA', 'N'} atoms
	 * - Filter these by angle (TODO - alter input points to be filterable!)
	 */
	@Test
	public void testFilterAtomLists() {
		double angle = 90;
		
		Chain chain = makeData();
		GroupPipe inPipe = new GroupPipe(chain);
		AtomListPipe triplePipe = new AtomListPipe();
		ScanAtomListsByLabel scanTriple = new ScanAtomListsByLabel(List.of("C", "CA", "N"), inPipe, triplePipe);
		FilterAtomListsByCondition filter = 
				new FilterAtomListsByCondition(new AtomAngleCondition(angle), triplePipe, new PrintAtomLists());
		runAll(List.of(scanTriple, filter));
	}
	
	private void runAll(List<Operator> pipeline) {
		for (Operator operator : pipeline) {
			operator.run();
		}
	}
	
	private Chain makeData() {
		Chain chain = new Chain();
		double x = 1;
		double y = 1;
		Vector p = new Vector(x, y, 0);
		int numberOfGroups = 3;
		for (int i = 0; i < numberOfGroups; i++) {
			Group group = new Group(i, "GLY");	// could add 1 to get a more realistic number ..
			p = addAtom(group, "N", p);
			p = addAtom(group, "C", p);
			p = addAtom(group, "CA", p);
			p = addAtom(group, "O", p);
			chain.addGroup(group);
		}
		
		return chain;
	}
	
	private Vector addAtom(Group group, String atomName, Vector p) {
		group.addAtom(new Atom(atomName, p));
		return nextP(p);
	}
	
	private Vector nextP(Vector p) {
		return new Vector(p.x() + 1, p.y() + 1, 0);
	}

}
