package tailor.experiment;

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
