package tailor.experiment.operator;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.structure.Atom;
import tailor.structure.Group;

public class ScanAtomByLabel implements Operator {
	
	// Atom label to search for // TODO - could be a general condition
	private String atomLabel;
	
	// Source to take from
	private Source<Group> source;
	
	private Sink<Atom> atomOutput;
	
	public ScanAtomByLabel(String atomLabel, Source<Group> source, Sink<Atom> atomOutput) {
		this.atomLabel = atomLabel;
		this.source = source;
		this.atomOutput = atomOutput;
	}
	
	public void run() {
		while (source.hasNext()) {
			Group group = source.getNext();
			for (Atom atom : group.getAtoms())	{ // could just use group.getAtomByName?
				if (atom.getName().equals(atomLabel)) {	// is this general enough?
					atomOutput.put(atom);
				}
			}
		}
	}

}
