package tailor.engine.physical;

import tailor.engine.logical.LogicalOperator;
import tailor.engine.logical.LogicalPlan;
import tailor.engine.logical.SelectChainByType;
import tailor.engine.logical.SelectGroupByType;

public class PhysicalPlanner {

	public PhysicalPlan plan(LogicalPlan logicalPlan) {
		PhysicalPlan physicalPlan = new PhysicalPlan();
		
		for (LogicalOperator root : logicalPlan.getRoots()) {
			walk(logicalPlan, root, physicalPlan);
		}
		
		return physicalPlan;
	}
	
	private void walk(LogicalPlan logicalPlan, LogicalOperator root, PhysicalPlan physicalPlan) {
		// really dumb, but a start
		if (root instanceof SelectChainByType selectChainByType) {
			PhysicalOperator chainFilter = new ChainFilter(selectChainByType.getType());
			for (LogicalOperator next : logicalPlan.getNext(root)) {
				if (next instanceof SelectGroupByType selectGroupByName) {
					PhysicalOperator groupFilter = new GroupFilter(selectGroupByName.getGroupType());
				}
			}
		}
	}
	
	
}
