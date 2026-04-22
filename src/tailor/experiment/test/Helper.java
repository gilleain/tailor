package tailor.experiment.test;

import java.util.ArrayList;
import java.util.List;

import tailor.api.Operator;
import tailor.api.Sink;
import tailor.description.AtomDescription;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;
import tailor.engine.operator.GroupSource;
import tailor.engine.plan.Plan;
import tailor.engine.plan.Result;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;

public class Helper {
	
	protected static void run(Chain chain, Plan plan) {
		List<Sink<Result>> inputs = plan.getInputPipes();
		GroupSource groupSource = new GroupSource(chain, inputs);
		List<Operator> fullPipeline = new ArrayList<>();
		fullPipeline.add(groupSource);
		fullPipeline.addAll(plan.getOperators());

		Helper.runAll(fullPipeline);
	}
	
	protected static void runAll(List<Operator> pipeline) {
		for (Operator operator : pipeline) {
			operator.run();
		}
	}
	
	protected static void describe(Plan plan) {	// TODO - remove
		plan.describe();
	}
	
	public static DescriptionPath pathTo(GroupDescription groupDescription, String atomLabel) {
		AtomDescription atomDescription = 
				groupDescription.getAtomDescriptions().stream()
				.filter(a -> a.getLabel().equals(atomLabel))
				.findFirst()
				.orElseThrow();
		return new DescriptionPath(groupDescription, atomDescription);
	}
	
	public static GroupDescription makeGroupDescription(String... atomLabels) {
		GroupDescription groupDescription = new GroupDescription();
		for (String atomLabel : atomLabels) {
			groupDescription.addAtomDescription(new AtomDescription(atomLabel));
		}
		return groupDescription;
	}
	
	protected static Chain makeData(int numberOfGroups) {
		List<String> groupNames = new ArrayList<>();
		for (int counter = 0; counter < numberOfGroups; counter++) {
			groupNames.add("GLY");
		}
		return makeData(groupNames);
	}
	
	protected static Chain makeData(List<String> groupNames) {	
		Chain chain = new Chain();
		Vector p = new Vector(1, 1, 1);
		for (int i = 0; i < groupNames.size(); i++) {
			String groupName = groupNames.get(i);
			Group group = new Group(i, groupName);	// could add 1 to get a more realistic number ..
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
		return new Vector(p.y() + 1.2, p.z() + 1.5, p.x() + 1.7);
	}

}
