package tailor.experiment.operator;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.condition.AtomPropertyCondition;
import tailor.structure.Atom;

public class FilterAtomsByCondition {
	
	AtomPropertyCondition condition;	// TODO - make more general
	
	private Source<Atom> input;
	
	private Sink<Atom> output;
	
	public FilterAtomsByCondition(AtomPropertyCondition condition, Source<Atom> input, Sink<Atom> output) {
		this.condition = condition;
		this.input = input;
		this.output = output;
	}

	public void run() {
		while (input.hasNext()) {
			Atom nextAtom = input.getNext();
			if (condition.accept(nextAtom)) {
				output.put(nextAtom);
			}
		}
	}

}
