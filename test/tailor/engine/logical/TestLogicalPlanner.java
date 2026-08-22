package tailor.engine.logical;

import java.util.List;

import org.junit.jupiter.api.Test;

import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;
import tailor.description.GroupDescriptionPath;
import tailor.measure.AbstractAtomListMeasure;
import tailor.measurement.DoubleMeasurement;
import tailor.operator.Helper;
import tailor.partition.AtomPartition;
import tailor.structure.Atom;
import tailor.structure.PolymerType;

public class TestLogicalPlanner {
	
	@Test
	public void testChainWithType() {
		ChainDescription chainDescription = new ChainDescription(null, PolymerType.PEPTIDE);
		GroupDescription group = Helper.makeGroupDescription("N");
		chainDescription.addGroupDescriptions(group);
		
		LogicalPlan plan = new LogicalPlanner().plan(chainDescription);
		
		System.out.println(plan);
	}
	
	@Test
	public void testNamedAndUnnamedResidues() {
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N");
		GroupDescription groupB = Helper.makeGroupDescriptionWithName("GLY", "CA");
		GroupDescription groupC = Helper.makeGroupDescription("C");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC);
//		chainDescription.addAtomListDescriptions(
//			new DummyAtomListDescription(pathTo(groupA, "N"), pathTo(groupB, "CA"))
//	    );
		
		LogicalPlan plan = new LogicalPlanner().plan(chainDescription);
		
		System.out.println(plan);
	}
	
	private class DummyAtomListDescription implements AtomListDescription {

		private AtomListMeasure measure;

		DummyAtomListDescription(GroupDescriptionPath... paths) {
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

		public DummyAtomListMeasure(GroupDescriptionPath... paths) {
			super("dummy", paths);
		}

		@Override
		public DoubleMeasurement measure(List<Atom> atoms) {
			return new DoubleMeasurement(1);
		}

	}

}
