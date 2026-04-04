package tailor.experiment.operator;

import java.util.List;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.api.TmpOperator;
import tailor.experiment.plan.Result;
import tailor.structure.Atom;

public class ScanAtomResultByLabel implements TmpOperator<Result, Result> {
	
	// Atom labels to search for // TODO - could be a general condition
	private List<String> atomLabels;
	
	// Source to take from
	private Source<Result> source;
	
	private Sink<Result> sink;
	
	public ScanAtomResultByLabel(List<String> atomLabels) {
		this.atomLabels = atomLabels;
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
				if (atomLabels.contains(atom.getName())) {	
					Result newResult = groupResult.copyWithoutAtoms();
					newResult.addAtom(atom);
					sink.put(newResult);
				}
			}
		}
	}

}
