package tailor.experiment.operator;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.structure.Atom;

public class FilterAtomListsByCondition implements Operator {
	
	private AtomListCondition condition;
	
	private Source<List<Atom>> input;
	
	private Sink<List<Atom>> output;
	
	public FilterAtomListsByCondition(AtomListCondition condition, Source<List<Atom>> input, Sink<List<Atom>> output) {
		this.condition = condition;
		this.input = input;
		this.output = output;
	}

	public void run() {
		while (input.hasNext()) {
			List<Atom> nextAtomList = input.getNext();
			if (condition.accept(nextAtomList)) {
				output.put(nextAtomList);
			}
		}
	}

}
