package tailor.experiment.test;

import java.util.ArrayList;
import java.util.List;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.description.AtomDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.operator.GroupSource;
import tailor.experiment.operator.ResultPipe;
import tailor.experiment.operator.ScanAtomResultByLabel;
import tailor.experiment.plan.Result;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;

public class Helper {
	
	protected static void run(Chain chain, List<Operator> pipeline) {
		// TODO - better
		List<Sink<Result>> inputs = new ArrayList<Sink<Result>>();
		for (Operator o : pipeline) {
			if (o instanceof ScanAtomResultByLabel scan) {
				ResultPipe input = new ResultPipe();
				scan.setSource(input);
				inputs.add(input);
			}
		}
		GroupSource groupSource = new GroupSource(chain, inputs);
		List<Operator> fullPipeline = new ArrayList<>();
		fullPipeline.add(groupSource);
		fullPipeline.addAll(pipeline);

		Helper.runAll(fullPipeline);
	}
	
	protected static void runAll(List<Operator> pipeline) {
		for (Operator operator : pipeline) {
			operator.run();
		}
	}
	
	protected static void describe(List<Operator> pipeline) {
		for (Operator operator : pipeline) {
			System.out.println(operator.description());
		}
	}
	
	protected static DescriptionPath pathTo(GroupDescription groupDescription, String atomLabel) {
		AtomDescription atomDescription = 
				groupDescription.getAtomDescriptions().stream()
				.filter(a -> a.getLabel().equals(atomLabel))
				.findFirst()
				.orElseThrow();
		return new DescriptionPath(groupDescription, atomDescription);
	}
	
	protected static GroupDescription makeGroupDescription(String... atomLabels) {
		GroupDescription groupDescription = new GroupDescription();
		for (String atomLabel : atomLabels) {
			groupDescription.addAtomDescription(new AtomDescription(atomLabel));
		}
		return groupDescription;
	}
	
	protected static Chain makeData(int numberOfGroups) {
		Chain chain = new Chain();
		double x = 1;
		double y = 1;
		Vector p = new Vector(x, y, 0);
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
