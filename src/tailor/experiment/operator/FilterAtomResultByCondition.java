package tailor.experiment.operator;

import java.util.List;
import java.util.logging.Logger;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.condition.AtomMatcher.Match;
import tailor.experiment.plan.Result;

public class FilterAtomResultByCondition extends AbstractPipeableOperator {
	
	private Logger logger = Logger.getLogger(FilterAtomResultByCondition.class.getName());
	
	/**
	 * Association of a matcher (to find atoms) and a condition (to match on them).
	 */
	public record ConditionMatcher(AtomListCondition condition, AtomMatcher matcher) {}
	
	private List<ConditionMatcher> conditionMatchers;
	
	public FilterAtomResultByCondition(List<ConditionMatcher> conditionMatchers) {
		this.conditionMatchers = conditionMatchers;
	}
	
	public void run() {
		int filterInCount = 0;
		int filterOutCount = 0;
		while (source.hasNext()) {
			Result nextResult = source.getNext();
			boolean isAccepted = true;
			for (ConditionMatcher conditionMatcher : conditionMatchers) {
				if (!matches(nextResult, conditionMatcher)) {
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
	
	private boolean matches(Result result, ConditionMatcher conditionMatcher) {
		boolean isMatch = true;
		for (Match match : conditionMatcher.matcher().extract(result)) {
			logger.fine("Checking " + match + " from " + result);
			if (conditionMatcher.condition.accept(match.getAtoms())) {
				logger.fine(" Match");
			} else {
				logger.fine(" NO Match " + result);
				isMatch = false;
			}
		}
		return isMatch;
	}

	@Override
	public String description() {
		List<String> conditionNames = conditionMatchers.stream().map(c -> c.getClass().getSimpleName()).toList();
		return "Filter: id[" + getId() + "] conditions:" + conditionNames;
	}
}
