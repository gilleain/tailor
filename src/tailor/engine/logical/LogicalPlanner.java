package tailor.engine.logical;

import java.util.Optional;

import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;
import tailor.structure.PolymerType;

public class LogicalPlanner {
	
	public LogicalPlan plan(ChainDescription chainDescription) {
		LogicalPlan plan = new LogicalPlan();
		
		PolymerType type = chainDescription.getType();
		LogicalOperator chainOperator;
		if (type == PolymerType.NONE) {
			chainOperator = new SelectAllChains();
		} else {
			chainOperator = new SelectChainByType(type);
		}
		plan.addRoot(chainOperator);
		
		for (GroupDescription groupDescription : chainDescription.getGroupDescriptions()) {
			Optional<String> groupName = groupDescription.getName();
			LogicalOperator groupOperator;
			if (groupName.isPresent()) {
				groupOperator = new SelectGroupByType(groupName.get());
			} else {
				groupOperator = new SelectAllResidues();
			}
			plan.addOperatorFrom(chainOperator, groupOperator);
			
			for (AtomDescription atomDescription : groupDescription.getAtomDescriptions()) {
				LogicalOperator atomOperator = new SelectAtomByName(atomDescription.getLabel());
				plan.addOperatorFrom(groupOperator, atomOperator);
			}
		}
		
		return plan;
	}

}
