package tailor.experiment.operator;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.api.TmpOperator;
import tailor.experiment.plan.Result;
import tailor.structure.Atom;

public class ScanAtomResultByLabel implements TmpOperator<Result, Result> {
	
	// Atom label to search for // TODO - could be a general condition
	private String atomLabel;
	
	// Source to take from
	private Source<Result> source;
	
	private Sink<Result> sink;
	
	public ScanAtomResultByLabel(String atomLabel) {
		this.atomLabel = atomLabel;
	}
	
	public void setSource(Source<Result> source) {
		this.source = source;
	}
	
	public void setSink(Sink<Result> sink) {
		this.sink = sink;
	}
	
	public void run() {
		while (source.hasNext()) {
			Result groupResult = source.getNext();
			for (Atom atom : groupResult.getAtoms())	{ // could just use group.getAtomByName?
				if (atom.getName().equals(atomLabel)) {	// is this general enough?
					Result newResult = groupResult.copyWithoutAtoms();
					newResult.addAtom(atom);
					sink.put(newResult);
				}
			}
		}
	}

}
