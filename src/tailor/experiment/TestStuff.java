package tailor.experiment;

import java.util.List;

import org.junit.Test;

import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;

public class TestStuff {
	
	@Test
	public void testScan() {
		PrintAtoms sink = new PrintAtoms();
		Chain chain = makeData();
		ScanAtomByLabel scan = new ScanAtomByLabel("O", chain, sink);
		scan.run();
	}
	
	@Test
	public void testCombine() {
		PrintAtomLists sink = new PrintAtomLists();
		Chain chain = makeData();
		
		AtomPipe oPipe = new AtomPipe();
		ScanAtomByLabel scanO = new ScanAtomByLabel("O", chain, oPipe);
		
		AtomPipe nPipe = new AtomPipe();
		ScanAtomByLabel scanN = new ScanAtomByLabel("N", chain, nPipe);
		
		CombineAtoms combineON = new CombineAtoms(List.of(oPipe, nPipe), sink);
		
		// run the pipeline
		scanO.run();
		scanN.run();
		combineON.run();
	}
	
	@Test
	public void testFilter() {
		double distance = 5;	// whatever
		
		Chain chain = makeData();
		
		AtomPipe oPipe = new AtomPipe();
		ScanAtomByLabel scanO = new ScanAtomByLabel("O", chain, oPipe);
		
		AtomPipe nPipe = new AtomPipe();
		ScanAtomByLabel scanN = new ScanAtomByLabel("N", chain, nPipe);
		
		AtomListPipe onPipe = new AtomListPipe();
		CombineAtoms combineON = new CombineAtoms(List.of(oPipe, nPipe), onPipe);
		FilterAtomListsByCondition filter = 
				new FilterAtomListsByCondition(new AtomDistanceCondition(distance), onPipe, new PrintAtomLists());
		
		// run the pipeline
		scanO.run();
		scanN.run();
		combineON.run();
		filter.run();
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
