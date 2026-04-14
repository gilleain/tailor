package tailor.experiment.api;

import java.util.List;

import tailor.experiment.condition.AtomPartition;
import tailor.structure.Atom;

public interface AtomListCondition {
	
	boolean accept(List<Atom> atoms);
	
	boolean accept(AtomPartition atomPartition);

}
