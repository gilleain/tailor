package tailor.experiment.operator;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.api.TmpOperator;
import tailor.experiment.plan.Result;
import tailor.structure.Atom;
import tailor.structure.Group;

public class ScanAtomResultByLabel implements TmpOperator<Group, Result> {
	
	// Atom label to search for // TODO - could be a general condition
	private String atomLabel;
	
	// Source to take from
	private Source<Group> source;
	
	private Sink<Result> sink;
	
	public ScanAtomResultByLabel(String atomLabel) {
		this.atomLabel = atomLabel;
	}
	
	public void setSource(Source<Group> source) {
		this.source = source;
	}
	
	public void setSink(Sink<Result> sink) {
		this.sink = sink;
	}
	
	public void run() {
		while (source.hasNext()) {
			Group group = source.getNext();
			for (Atom atom : group.getAtoms())	{ // could just use group.getAtomByName?
				if (atom.getName().equals(atomLabel)) {	// is this general enough?
					Result newResult = new Result(group, atom);
					sink.put(newResult);
				}
			}
		}
	}

}
