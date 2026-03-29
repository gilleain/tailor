package tailor.experiment.test;

import java.util.List;

import org.junit.Test;

import tailor.experiment.api.Operator;
import tailor.experiment.description.AtomDescription;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.plan.Planner;

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
	
	private GroupDescription makeGroupDescription(String... atomLabels) {
		GroupDescription groupDescription = new GroupDescription();
		for (String atomLabel : atomLabels) {
			groupDescription.addAtomDescription(new AtomDescription(atomLabel));
		}
		return groupDescription;
	}

}
