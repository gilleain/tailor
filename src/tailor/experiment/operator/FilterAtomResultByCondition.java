package tailor.experiment.operator;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.api.PipeableOperator;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.plan.Result;
import tailor.structure.Atom;

public class FilterAtomResultByCondition implements PipeableOperator<Result, Result> {
	
	private AtomListCondition condition;
	
	private AtomMatcher matcher;
	
	private Source<Result> input;
	
	private Sink<Result> output;
	
	public FilterAtomResultByCondition(AtomListCondition condition, AtomMatcher matcher) {
		this.condition = condition;
		this.matcher = matcher;
	}

	public void run() {
		while (input.hasNext()) {
			Result nextResult = input.getNext();
			for (List<Atom> match : matcher.extract(nextResult)) {
				System.out.println("Checking " + match + " from " + nextResult);
				if (condition.accept(match)) {
					output.put(nextResult);
				} else {
					System.out.println("Filtering OUT " + nextResult);
				}
			}
		}
	}

	@Override
	public void setSource(Source<Result> source) {
		this.input = source;
	}

	@Override
	public void setSink(Sink<Result> sink) {
		this.output = sink;
	}

}
