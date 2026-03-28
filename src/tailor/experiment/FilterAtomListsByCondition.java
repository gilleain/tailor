package tailor.experiment;

import java.util.List;

import tailor.structure.Atom;

public class FilterAtomListsByCondition {
	
	AtomDistanceCondition condition;	// TODO - make more general
	
	private Source<List<Atom>> input;
	
	private Sink<List<Atom>> output;
	
	public FilterAtomListsByCondition(AtomDistanceCondition condition, Source<List<Atom>> input, Sink<List<Atom>> output) {
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
