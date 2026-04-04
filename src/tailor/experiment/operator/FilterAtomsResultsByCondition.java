package tailor.experiment.operator;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.api.TmpOperator;
import tailor.experiment.condition.AtomPropertyCondition;
import tailor.experiment.plan.Result;
import tailor.structure.Atom;

public class FilterAtomsResultsByCondition implements TmpOperator<Result, Result> {
	
	AtomPropertyCondition condition;	// TODO - make more general
	
	private Source<Result> input;
	
	private Sink<Result> output;
	
	public FilterAtomsResultsByCondition(AtomPropertyCondition condition) {
		this.condition = condition;
	}

	public void run() {
		while (input.hasNext()) {
			Result nextResult = input.getNext();
			Atom atom = nextResult.getAtoms().get(0); // TODO - match the atom
			if (condition.accept(atom)) {
				output.put(nextResult);
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
