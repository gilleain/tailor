package tailor.experiment.operator;

import java.util.List;
import java.util.logging.Logger;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomPartition;
import tailor.experiment.plan.Result;

public class FilterAtomResultByCondition extends AbstractPipeableOperator {
	
	private Logger logger = Logger.getLogger(FilterAtomResultByCondition.class.getName());
	
	
	private List<AtomListCondition> conditions;
	
	public FilterAtomResultByCondition(List<AtomListCondition> conditions) {
		this.conditions = conditions;
	}
	
	public void run() {
		int filterInCount = 0;
		int filterOutCount = 0;
		while (source.hasNext()) {
			Result nextResult = source.getNext();
			boolean isAccepted = true;
			AtomPartition atomPartition = nextResult.getAtomPartition();
			for (AtomListCondition condition : conditions) {
				if (!condition.accept(atomPartition)) {
					logger.fine(condition + " failed for " + atomPartition + " of " + nextResult);
					isAccepted = false;
					break;
				}
			}
			if (isAccepted) {
				logger.fine("Filtering IN " + nextResult);
				sink.put(nextResult);
				filterInCount++;
			} else {
				logger.fine("Filtering OUT " + nextResult);
				filterOutCount++;
			}
		}
		logger.info(description() + " filtered: IN " + filterInCount + " OUT " + filterOutCount);
	}
	
	@Override
	public String description() {
		List<String> conditionNames = conditions.stream().map(c -> c.getClass().getSimpleName()).toList();
		return "Filter: id[" + getId() + "] conditions:" + conditionNames;
	}
}
