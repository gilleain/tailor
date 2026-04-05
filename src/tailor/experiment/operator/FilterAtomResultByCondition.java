package tailor.experiment.operator;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.plan.Result;
import tailor.structure.Atom;

public class FilterAtomResultByCondition extends AbstractPipeableOperator {
	
	private AtomListCondition condition;
	
	private AtomMatcher matcher;
	
	public FilterAtomResultByCondition(AtomListCondition condition, AtomMatcher matcher) {
		this.condition = condition;
		this.matcher = matcher;
	}

	public void run() {
		while (source.hasNext()) {
			Result nextResult = source.getNext();
			for (List<Atom> match : matcher.extract(nextResult)) {
				System.out.println("Checking " + match + " from " + nextResult);
				if (condition.accept(match)) {
					sink.put(nextResult);
				} else {
					System.out.println("Filtering OUT " + nextResult);
				}
			}
		}
	}

	@Override
	public String description() {
		return "Filter: id[" + getId() + "] condition:" + condition.getClass().getSimpleName();
	}
}
