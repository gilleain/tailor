package tailor.experiment.api;

import java.util.List;

import tailor.structure.Atom;

public interface AtomListCondition {
	
	boolean accept(List<Atom> atoms);

}
