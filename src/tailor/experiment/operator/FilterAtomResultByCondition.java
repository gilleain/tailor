package tailor.experiment.operator;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.plan.Result;
import tailor.structure.Atom;

public class FilterAtomResultByCondition extends AbstractPipeableOperator {
	
	/**
	 * Association of a matcher (to find atoms) and a condition (to match on them).
	 */
	public record ConditionMatcher(AtomListCondition condition, AtomMatcher matcher) {}
	
	private List<ConditionMatcher> conditionMatchers;
	
	public FilterAtomResultByCondition(List<ConditionMatcher> conditionMatchers) {
		this.conditionMatchers = conditionMatchers;
	}
	
	public void run() {
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
				System.out.println("Filtering IN " + nextResult);
				sink.put(nextResult);
			}
		}
	}
	
	private boolean matches(Result result, ConditionMatcher conditionMatcher) {
		boolean isMatch = true;
		for (List<Atom> match : conditionMatcher.matcher().extract(result)) {
			System.out.println("Checking " + match + " from " + result);
			if (conditionMatcher.condition.accept(match)) {
				//
			} else {
				System.out.println("Filtering OUT " + result);
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
