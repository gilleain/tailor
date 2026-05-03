package tailor.operator;

import static org.junit.Assert.assertEquals;
import static tailor.operator.Helper.pathTo;

import java.util.List;

import org.junit.Test;

import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.api.Operator;
import tailor.condition.AtomPartition;
import tailor.description.ChainDescription;
import tailor.description.DescriptionFactory;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;
import tailor.engine.operator.CombineResults;
import tailor.engine.operator.FilterAtomResultByCondition;
import tailor.engine.operator.FilterGroupByDescription;
import tailor.engine.operator.Measurer;
import tailor.engine.operator.PrintAdapter;
import tailor.engine.operator.ScanAtomResultByLabel;
import tailor.engine.plan.Plan;
import tailor.engine.plan.Planner;
import tailor.measure.AbstractAtomListMeasure;
import tailor.measurement.DoubleMeasurement;
import tailor.structure.Atom;

public class TestPlanner {
	
	private class DummyAtomListDescription implements AtomListDescription {
		
		private AtomListMeasure measure;
		
		DummyAtomListDescription(DescriptionPath... paths) {
			this.measure = new DummyAtomListMeasure(paths);
		}

		
		public AtomListMeasure createMeasure() {
			return this.measure;
		}

		@Override
		public List<GroupDescription> getGroupDescriptions() {
			return this.measure.getGroupDescriptions();
		}

		@Override
		public boolean apply(AtomPartition atomPartition) {
			return true;
		}
		
	}
	
	private class DummyAtomListMeasure extends AbstractAtomListMeasure {

		public DummyAtomListMeasure(DescriptionPath... paths) {
			super("dummy", paths);
		}

		@Override
		public DoubleMeasurement measure(List<Atom> atoms) {
			return new DoubleMeasurement(1);
		}
		
	}
	
	
	private static long countOf(Plan plan, Class<? extends Operator> type) {
		return plan.getOperators().stream().filter(type::isInstance).count();
	}

	@Test
	public void testOneNamedGroupOneUnnamed() {
		DescriptionFactory df = new DescriptionFactory("A");
		df.addResidueToChainWithoutAtoms("A", "GLY").addAtomDescription("N");
		df.addResidueToChainWithoutAtoms("A").addAtomDescription("O");

		Plan plan = new Planner().plan(df.getChainDescription("A"));
		Helper.describe(plan);

		// named group: FilterGroupByDescription + ScanAtomResultByLabel; unnamed: ScanAtomResultByLabel
		// two separate components joined by CombineResults, then PrintAdapter
		assertEquals(5, plan.getOperators().size());
		assertEquals(2, plan.getStartPoints().size());
		assertEquals(1, countOf(plan, FilterGroupByDescription.class));
		assertEquals(2, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));
	}
	
	@Test
	public void testTwoNamedGroups() {
		DescriptionFactory df = new DescriptionFactory("A");
		df.addResidueToChainWithoutAtoms("A", "GLY").addAtomDescription("N");
		df.addResidueToChainWithoutAtoms("A", "HIS").addAtomDescription("O");

		Plan plan = new Planner().plan(df.getChainDescription("A"));
		Helper.describe(plan);

		// each named group: FilterGroupByDescription + ScanAtomResultByLabel
		// two separate components joined by CombineResults, then PrintAdapter
		assertEquals(6, plan.getOperators().size());
		assertEquals(2, plan.getStartPoints().size());
		assertEquals(2, countOf(plan, FilterGroupByDescription.class));
		assertEquals(2, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));
	}
	
	@Test
	public void testTwoResiduesWithSingleAtoms() {
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addGroupDescription(Helper.makeGroupDescription("N"));
		chainDescription.addGroupDescription(Helper.makeGroupDescription("O"));

		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// two unnamed groups → two ScanAtomResultByLabel start points
		// joined by CombineResults, then PrintAdapter
		assertEquals(4, plan.getOperators().size());
		assertEquals(2, plan.getStartPoints().size());
		assertEquals(2, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));
	}
	
	@Test
	public void testTwoResiduesWithMultipleAtoms() {
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addGroupDescription(Helper.makeGroupDescription("N", "CA"));
		chainDescription.addGroupDescription(Helper.makeGroupDescription("O", "C"));

		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// same structure as single-atom case: two scanners joined and printed
		assertEquals(4, plan.getOperators().size());
		assertEquals(2, plan.getStartPoints().size());
		assertEquals(2, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

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
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N");
		GroupDescription groupB = Helper.makeGroupDescription("CA");
		GroupDescription groupC = Helper.makeGroupDescription("C");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC);
		chainDescription.addAtomListDescriptions(
			new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupB, "CA"))
	    );
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// groupA+groupB connected via outer condition → component with Combine+Filter
		// groupC is its own component; both joined by a second CombineResults, then PrintAdapter
		assertEquals(7, plan.getOperators().size());
		assertEquals(3, plan.getStartPoints().size());
		assertEquals(3, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(2, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

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
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA");
		GroupDescription groupB = Helper.makeGroupDescription("C");
		chainDescription.addGroupDescriptions(groupA, groupB);
		chainDescription.addAtomListDescriptions(
				new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupA, "CA"))
	    );
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// inner condition on groupA → Scan + Filter for groupA; groupB is Scan only
		// two separate components joined by CombineResults, then PrintAdapter
		assertEquals(5, plan.getOperators().size());
		assertEquals(2, plan.getStartPoints().size());
		assertEquals(2, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(1, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

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
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA");
		GroupDescription groupB = Helper.makeGroupDescription("C");
		chainDescription.addGroupDescriptions(groupA, groupB);
		chainDescription.addAtomListDescriptions(
			new DummyAtomListDescription(pathTo(groupA, "CA"), pathTo(groupB, "C"))
	    );
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// outer condition connects groupA and groupB → one component: Scan+Scan+Combine+Filter
		// single component so no reduction CombineResults; then PrintAdapter
		assertEquals(5, plan.getOperators().size());
		assertEquals(2, plan.getStartPoints().size());
		assertEquals(2, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(1, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO", "HIS")), plan);
	}

	/**
	 * - Create a description with one group and two atoms {N, CA}
	 * - Add a distance description to the group
	 */
	@Test
	public void testInnerGroupFilteringDistance() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA");
		chainDescription.addGroupDescription(groupA);
		chainDescription.addAtomListDescriptions(
			new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupA, "CA"))
		);
		
		Plan plan  = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// single group with inner condition → Scan + Filter; no join needed; then PrintAdapter
		assertEquals(3, plan.getOperators().size());
		assertEquals(1, plan.getStartPoints().size());
		assertEquals(1, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

		Helper.run(Helper.makeData(3), plan);
	}
	
	/**
	 * - Create a description with one group and three atoms {N, CA, C}
	 * - Add an angle description to the group
	 */
	@Test
	public void testInnerGroupFilteringThreeAtoms() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA", "C");
		chainDescription.addGroupDescription(groupA);
		groupA.addAtomListDescriptions(
			new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupA, "CA"), pathTo(groupA, "C"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// single group with inner condition → Scan + Filter; no join needed; then PrintAdapter
		assertEquals(3, plan.getOperators().size());
		assertEquals(1, plan.getStartPoints().size());
		assertEquals(1, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO")), plan);
	}

	/**
	 * - Create a description with two groups - one {N, CA}, the other {O, C}
	 * - Add a distance description to one of the groups
	 */
	@Test
	public void testInnerGroupFilteringOnOneResidue() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA");
		chainDescription.addGroupDescription(groupA);
		chainDescription.addAtomListDescriptions(
			new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupA, "CA"))
		);
		
		chainDescription.addGroupDescription(Helper.makeGroupDescription("O", "C"));
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// groupA has inner filter; groupB has no conditions
		// two separate components joined by CombineResults, then PrintAdapter
		assertEquals(5, plan.getOperators().size());
		assertEquals(2, plan.getStartPoints().size());
		assertEquals(2, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(1, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

		Helper.run(Helper.makeData(3), plan);
	}

	@Test
	public void testInnerGroupMultiFilteringOnOneResidue() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA", "C", "O");
		chainDescription.addGroupDescription(groupA);
		chainDescription.addAtomListDescriptions(
			new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupA, "CA")),
			new DummyAtomListDescription(pathTo(groupA, "C"), pathTo(groupA, "O"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// two inner conditions on the same group are combined into one FilterAtomResultByCondition
		assertEquals(3, plan.getOperators().size());
		assertEquals(1, plan.getStartPoints().size());
		assertEquals(1, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

		Helper.run(Helper.makeData(3), plan);
	}

	@Test
	public void testOuterGroupFilteringDisconnectedPairs() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N");
		GroupDescription groupB = Helper.makeGroupDescription("CA");
		GroupDescription groupC = Helper.makeGroupDescription("C");
		GroupDescription groupD = Helper.makeGroupDescription("O");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC, groupD);
		chainDescription.addAtomListDescriptions(
				new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupB, "CA")),
				new DummyAtomListDescription(pathTo(groupC, "C"), pathTo(groupD, "O"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// two disconnected pair components: each has Scan+Scan+Combine+Filter
		// the two component outputs are joined by a final CombineResults, then PrintAdapter
		assertEquals(10, plan.getOperators().size());
		assertEquals(4, plan.getStartPoints().size());
		assertEquals(4, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(2, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(3, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO", "HIS", "ASP", "GLN")), plan);
	}
	
	@Test
	public void testOuterGroupFilteringConnectedPairsDistinctLabels() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N");
		GroupDescription groupB = Helper.makeGroupDescription("CA");
		GroupDescription groupC = Helper.makeGroupDescription("C");
		GroupDescription groupD = Helper.makeGroupDescription("O");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC, groupD);
		chainDescription.addAtomListDescriptions(
				new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupB, "CA")),
				new DummyAtomListDescription(pathTo(groupB, "CA"), pathTo(groupC, "C")),
				new DummyAtomListDescription(pathTo(groupC, "C"), pathTo(groupD, "O"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// all four groups connected in a chain via three conditions → one component
		// Scan×4 + CombineResults + FilterAtomResultByCondition + PrintAdapter
		assertEquals(7, plan.getOperators().size());
		assertEquals(4, plan.getStartPoints().size());
		assertEquals(4, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(1, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO", "HIS")), plan);
	}
	
	@Test
	public void testInnerGroupFilteringConnectedPairsSimilarLabels() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "O");
		GroupDescription groupB = Helper.makeGroupDescription("N", "O");
		chainDescription.addGroupDescriptions(groupA, groupB);
		chainDescription.addAtomListDescriptions(
				new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupA, "O")),
				new DummyAtomListDescription(pathTo(groupB, "N"), pathTo(groupB, "O"))
		);
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// each group has its own inner condition → Scan+Filter for each; joined by CombineResults
		assertEquals(6, plan.getOperators().size());
		assertEquals(2, plan.getStartPoints().size());
		assertEquals(2, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(2, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(1, countOf(plan, CombineResults.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO")), plan);
	}

	@Test
	public void testMeasure() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N", "CA", "C");
		chainDescription.addGroupDescription(groupA);
		AtomListDescription atomListDescriptionA = 
				new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupA, "CA"));
		AtomListDescription atomListDescriptionB = 
				new DummyAtomListDescription(pathTo(groupA, "CA"), pathTo(groupA, "C"));
		groupA.addAtomListDescriptions(atomListDescriptionA, atomListDescriptionB);
		chainDescription.addAtomListMeasures(
				atomListDescriptionA.createMeasure(), atomListDescriptionB.createMeasure());
		
		Plan plan = new Planner().plan(chainDescription);
		Helper.describe(plan);

		// single group with two inner conditions (combined into one Filter) + Measurer + PrintAdapter
		assertEquals(4, plan.getOperators().size());
		assertEquals(1, plan.getStartPoints().size());
		assertEquals(1, countOf(plan, ScanAtomResultByLabel.class));
		assertEquals(1, countOf(plan, FilterAtomResultByCondition.class));
		assertEquals(1, countOf(plan, Measurer.class));
		assertEquals(1, countOf(plan, PrintAdapter.class));

		Helper.run(Helper.makeData(List.of("GLY", "SER", "PRO")), plan);
	}

}
