package tailor.experiment.test;

import static tailor.experiment.test.Helper.pathTo;

import java.util.List;

import org.junit.Test;

import tailor.experiment.api.Operator;
import tailor.experiment.description.AtomAngleDescription;
import tailor.experiment.description.AtomDistanceDescription;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.plan.Planner;
import tailor.structure.Chain;

public class TestPlanner {
	
	@Test
	public void testSingles() {
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addGroupDescription(Helper.makeGroupDescription("N"));
		chainDescription.addGroupDescription(Helper.makeGroupDescription("O"));
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
	}
	
	@Test
	public void testMultiples() {
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addGroupDescription(Helper.makeGroupDescription("N", "CA"));
		chainDescription.addGroupDescription(Helper.makeGroupDescription("O", "C"));
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
	}
	
	/**
	 * - Create a description with one group and two atoms {N, CA}
	 * - Add a distance description to the group
	 */
	@Test
	public void testInnerGroupFilteringDistance() {
		double distance = 5.0;	 // w/e
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA");
		chainDescription.addGroupDescription(groupA);
		chainDescription.addAtomSetDescription(
			new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupA, "CA"))
		);
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
		
		Chain chain = Helper.makeData(3);
		Helper.run(chain, pipeline);
	}
	
	/**
	 * - Create a description with one group and three atoms {N, CA, C}
	 * - Add an angle description to the group
	 */
	@Test
	public void testInnerGroupFilteringAngle() {
		double angle = 45.0;	 // w/e
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA", "C");
		chainDescription.addGroupDescription(groupA);
		chainDescription.addAtomSetDescription(
			new AtomAngleDescription(angle, pathTo(groupA, "N"), pathTo(groupA, "CA"), pathTo(groupA, "C"))
		);
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
		
//		Chain chain = Helper.makeData(3);
//		Helper.run(chain, pipeline);
	}
	
	/**
	 * - Create a description with two groups - one {N, CA}, the other {O, C}
	 * - Add a distance description to one of the groups
	 */
	@Test
	public void testInnerGroupFilteringOnOneResidue() {
		double distance = 5.0;	 // w/e
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA");
		chainDescription.addGroupDescription(groupA);
		chainDescription.addAtomSetDescription(
			new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupA, "CA"))
		);
		
		chainDescription.addGroupDescription(Helper.makeGroupDescription("O", "C"));
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
		
		Helper.run(Helper.makeData(3), pipeline);
	}
	
	@Test
	public void testOuterGroupFilteringDisconnectedPairs() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("O");
		GroupDescription groupB = Helper.makeGroupDescription("O");
		GroupDescription groupC = Helper.makeGroupDescription("O");
		GroupDescription groupD = Helper.makeGroupDescription("O");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC, groupD);
		chainDescription.addAtomSetDescription(new AtomDistanceDescription(1, pathTo(groupA, "O"), pathTo(groupB, "O")));
		chainDescription.addAtomSetDescription(new AtomDistanceDescription(1, pathTo(groupC, "O"), pathTo(groupD, "O")));
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
		Helper.run(Helper.makeData(3), pipeline);
	}
	
	@Test
	public void testOuterGroupFilteringConnectedPairs() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("O");
		GroupDescription groupB = Helper.makeGroupDescription("O");
		GroupDescription groupC = Helper.makeGroupDescription("O");
		GroupDescription groupD = Helper.makeGroupDescription("O");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC, groupD);
		chainDescription.addAtomSetDescription(new AtomDistanceDescription(1, pathTo(groupA, "O"), pathTo(groupB, "O")));
		chainDescription.addAtomSetDescription(new AtomDistanceDescription(1, pathTo(groupB, "O"), pathTo(groupC, "O")));
		chainDescription.addAtomSetDescription(new AtomDistanceDescription(1, pathTo(groupC, "O"), pathTo(groupD, "O")));
		
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
		
		Helper.run(Helper.makeData(3), pipeline);
	}

}
