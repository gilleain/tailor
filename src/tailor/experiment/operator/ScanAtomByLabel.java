package tailor.experiment.operator;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;

public class ScanAtomByLabel implements Operator {
	
	// Atom label to search for // TODO - could be a general condition
	private String atomLabel;
	
	// Chain to search in - should be a more generic source
	private Chain chain;
	
	private Sink<Atom> atomOutput;
	
	public ScanAtomByLabel(String atomLabel, Chain chain, Sink<Atom> atomOutput) {
		this.atomLabel = atomLabel;
		this.chain = chain;
		this.atomOutput = atomOutput;
	}
	
	public void run() {
		for (Group group : chain.getGroups()) {
			for (Atom atom : group.getAtoms())	{ // could just use group.getAtomByName?
				if (atom.getName().equals(atomLabel)) {	// is this general enough?
					atomOutput.put(atom);
				}
			}
		}
	}

}
