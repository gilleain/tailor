package tailor.experiment.test;

import java.util.List;

import tailor.experiment.api.Operator;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;

public class Helper {
	
	protected static void runAll(List<Operator> pipeline) {
		for (Operator operator : pipeline) {
			operator.run();
		}
	}
	
	protected static Chain makeData() {
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
	
	private static Vector addAtom(Group group, String atomName, Vector p) {
		group.addAtom(new Atom(atomName, p));
		return nextP(p);
	}
	
	private static Vector nextP(Vector p) {
		return new Vector(p.x() + 1, p.y() + 1, 0);
	}

}
