package tailor.engine.logical;

import org.junit.jupiter.api.Test;

import tailor.engine.physical.PhysicalPlan;
import tailor.engine.physical.PhysicalPlanner;

public class TestPhysicalPlanner {
	
	@Test
	public void testSinglePath() {
		LogicalPlan logicalPlan = new LogicalPlan();
		SelectAllChains chainOp = new SelectAllChains();
		logicalPlan.addOperator(chainOp);
		SelectGroupByType resOp = new SelectGroupByType("GLY");
		logicalPlan.addOperatorFrom(chainOp, resOp);
		
		PhysicalPlan physicalPlan = new PhysicalPlanner().plan(logicalPlan);
		
		System.out.println(physicalPlan);
	}

}
