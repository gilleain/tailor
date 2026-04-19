package tailor.experiment.api;

import java.util.List;

import tailor.experiment.condition.AtomPartition;
import tailor.structure.Atom;

public interface AtomListMeasure {
	
	double measure(List<Atom> atoms);
	
	double measure(AtomPartition atomPartition);

}
