package tailor.experiment.test;

import static tailor.experiment.test.Helper.pathTo;

import java.util.List;

import org.junit.Test;

import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.description.atom.AtomAngleDescription;
import tailor.experiment.description.atom.AtomDistanceDescription;
import tailor.experiment.plan.Plan;
import tailor.experiment.plan.Planner;

public class TestPlanner {
	
	@Test
	public void testTwoResiduesWithSingleAtoms() {
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addGroupDescription(Helper.makeGroupDescription("N"));
		chainDescription.addGroupDescription(Helper.makeGroupDescription("O"));
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
	}
	
	@Test
	public void testTwoResiduesWithMultipleAtoms() {
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addGroupDescription(Helper.makeGroupDescription("N", "CA"));
		chainDescription.addGroupDescription(Helper.makeGroupDescription("O", "C"));
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO")), plan);
	}
	
	/**
	 * Make:
	 * - THREE atoms, with different labels
	 * - Attached to THREE residues 
	 * - A condition between two atoms in DIFFERENT residues
	 */
	@Test
	public void testTrioA() {
		double distance = 10.0;
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N");
		GroupDescription groupB = Helper.makeGroupDescription("CA");
		GroupDescription groupC = Helper.makeGroupDescription("C");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC);
		chainDescription.addAtomListDescriptions(
			new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupB, "CA"))
	    );
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
		
		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO", "HIS")), plan);
	}
	
	/**
	 * Make:
	 * - THREE atoms, with different labels
	 * - Attached to TWO residues 
	 * - A condition between two atoms in SAME residue
	 */
	@Test
	public void testTrioB() {
		double distance = 10.0;
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA");
		GroupDescription groupB = Helper.makeGroupDescription("C");
		chainDescription.addGroupDescriptions(groupA, groupB);
		chainDescription.addAtomListDescriptions(
			new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupA, "CA"))
	    );
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
		
		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO", "HIS")), plan);
	}
	
	/**
	 * Make:
	 * - THREE atoms, with different labels
	 * - Attached to TWO residues 
	 * - A condition between two atoms in DIFFERENT residues
	 */
	@Test
	public void testTrioC() {
		double distance = 10.0;
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA");
		GroupDescription groupB = Helper.makeGroupDescription("C");
		chainDescription.addGroupDescriptions(groupA, groupB);
		chainDescription.addAtomListDescriptions(
			new AtomDistanceDescription(distance, pathTo(groupA, "CA"), pathTo(groupB, "C"))
	    );
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
		
		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO", "HIS")), plan);
	}
	
	/**
	 * - Create a description with one group and two atoms {N, CA}
	 * - Add a distance description to the group
	 */
	@Test
	public void testInnerGroupFilteringDistance() {
		double distance = 7.0;	 // w/e
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA");
		chainDescription.addGroupDescription(groupA);
		chainDescription.addAtomListDescriptions(
			new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupA, "CA"))
		);
		
		Plan plan  = new Planner().plan(chainDescription);
		Helper.describe(plan);
		Helper.run(Helper.makeData(3), plan);
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
		groupA.addAtomListDescriptions(
			new AtomAngleDescription(angle, pathTo(groupA, "N"), pathTo(groupA, "CA"), pathTo(groupA, "C"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO")), plan);
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
		chainDescription.addAtomListDescriptions(
			new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupA, "CA"))
		);
		
		chainDescription.addGroupDescription(Helper.makeGroupDescription("O", "C"));
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
		Helper.run(Helper.makeData(3), plan);
	}
	
	@Test
	public void testInnerGroupMultiFilteringOnOneResidue() {
		double distance = 5.0;	 // w/e
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA", "C", "O");
		chainDescription.addGroupDescription(groupA);
		chainDescription.addAtomListDescriptions(
			new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupA, "CA")),
			new AtomDistanceDescription(distance, pathTo(groupA, "C"), pathTo(groupA, "O"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
		Helper.run(Helper.makeData(3), plan);
	}
	
	@Test
	public void testOuterGroupFilteringDisconnectedPairs() {
		double distance = 30.0;	// w/e
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N");
		GroupDescription groupB = Helper.makeGroupDescription("CA");
		GroupDescription groupC = Helper.makeGroupDescription("C");
		GroupDescription groupD = Helper.makeGroupDescription("O");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC, groupD);
		chainDescription.addAtomListDescriptions(
				new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupB, "CA")),
				new AtomDistanceDescription(distance, pathTo(groupC, "C"), pathTo(groupD, "O"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO", "HIS")), plan);
	}
	
	@Test
	public void testOuterGroupFilteringConnectedPairsDistinctLabels() {
		double distance = 10.0;	// w/e
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N");
		GroupDescription groupB = Helper.makeGroupDescription("CA");
		GroupDescription groupC = Helper.makeGroupDescription("C");
		GroupDescription groupD = Helper.makeGroupDescription("O");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC, groupD);
		chainDescription.addAtomListDescriptions(
				new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupB, "CA")),
				new AtomDistanceDescription(distance, pathTo(groupB, "CA"), pathTo(groupC, "C")),
				new AtomDistanceDescription(distance, pathTo(groupC, "C"), pathTo(groupD, "O"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO", "HIS")), plan);
	}
	
	@Test
	public void testInnerGroupFilteringConnectedPairsSimilarLabels() {
		double distance = 10.0;	// w/e
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "O");
		GroupDescription groupB = Helper.makeGroupDescription("N", "O");
		chainDescription.addGroupDescriptions(groupA, groupB);
		chainDescription.addAtomListDescriptions(
				new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupA, "O")),
				new AtomDistanceDescription(distance, pathTo(groupB, "N"), pathTo(groupB, "O"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);
		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO")), plan);
	}

}
