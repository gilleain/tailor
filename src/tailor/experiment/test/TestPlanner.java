package tailor.experiment.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.description.AtomDescription;
import tailor.experiment.description.AtomDistanceDescription;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.operator.GroupSource;
import tailor.experiment.operator.ResultPipe;
import tailor.experiment.operator.ScanAtomResultByLabel;
import tailor.experiment.plan.Planner;
import tailor.experiment.plan.Result;
import tailor.structure.Chain;

public class TestPlanner {
	
	@Test
	public void testSingles() {
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addGroupDescription(makeGroupDescription("N"));
		chainDescription.addGroupDescription(makeGroupDescription("O"));
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
	}
	
	@Test
	public void testMultiples() {
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addGroupDescription(makeGroupDescription("N", "CA"));
		chainDescription.addGroupDescription(makeGroupDescription("O", "C"));
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
	}
	
	/**
	 * - Create a description with one groups - {N, CA}
	 * - Add a distance description to the group
	 */
	@Test
	public void testInternalGroupFilteringSimple() {
		double distance = 5.0;	 // w/e
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = makeGroupDescription("N", "CA");
		chainDescription.addGroupDescription(groupA);
		chainDescription.addAtomSetDescription(
				new AtomDistanceDescription(distance, 
						new DescriptionPath(
								groupA, groupA.getAtomDescriptions().get(0)),
						new DescriptionPath(
								groupA, groupA.getAtomDescriptions().get(1))));
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
		
		Chain chain = Helper.makeData(3);
		
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
	
	/**
	 * - Create a description with two groups - one {N, CA}, the other {O, C}
	 * - Add a distance description to one of the groups
	 */
	@Test
	public void testInternalGroupFilteringOnOneResidue() {
		double distance = 5.0;	 // w/e
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = makeGroupDescription("N", "CA");
		chainDescription.addGroupDescription(groupA);
		chainDescription.addAtomSetDescription(
				new AtomDistanceDescription(distance, 
						new DescriptionPath(
								groupA, groupA.getAtomDescriptions().get(0)),
						new DescriptionPath(
								groupA, groupA.getAtomDescriptions().get(1))));
		
		chainDescription.addGroupDescription(makeGroupDescription("O", "C"));
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
		
		Chain chain = Helper.makeData(3);
		
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
	
	private GroupDescription makeGroupDescription(String... atomLabels) {
		GroupDescription groupDescription = new GroupDescription();
		for (String atomLabel : atomLabels) {
			groupDescription.addAtomDescription(new AtomDescription(atomLabel));
		}
		return groupDescription;
	}

}
