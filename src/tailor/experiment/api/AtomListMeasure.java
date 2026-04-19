package tailor.experiment.api;

import tailor.experiment.condition.AtomPartition;

/**
 * Measurement to make on a list of atoms.
 */
public interface AtomListMeasure {
	
	Measurement measure(AtomPartition atomPartition);

}
