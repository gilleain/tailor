package tailor.experiment.operator;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.api.TmpOperator;
import tailor.structure.Atom;
import tailor.structure.Group;

public class ScanAtomByLabel implements TmpOperator<Group, Atom> {
	
	// Atom label to search for // TODO - could be a general condition
	private String atomLabel;
	
	// Source to take from
	private Source<Group> source;
	
	private Sink<Atom> sink;
	
	public ScanAtomByLabel(String atomLabel, Source<Group> source, Sink<Atom> sink) {
		this.atomLabel = atomLabel;
		this.source = source;
		this.sink = sink;
	}
	
	public void setSource(Source<Group> source) {
		this.source = source;
	}
	
	public void setSink(Sink<Atom> sink) {
		this.sink = sink;
	}
	
	public void run() {
		while (source.hasNext()) {
			Group group = source.getNext();
			for (Atom atom : group.getAtoms())	{ // could just use group.getAtomByName?
				if (atom.getName().equals(atomLabel)) {	// is this general enough?
					sink.put(atom);
				}
			}
		}
	}

}
