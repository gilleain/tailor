package tailor.experiment;

import tailor.structure.Atom;

public class AtomPropertyCondition {
	
	private String label;	// TODO - make more general
	
	public AtomPropertyCondition(String label) {
		this.label = label;
	}
	
	public boolean accept(Atom a) {
		return a.getName().equals(label);
	}

}
