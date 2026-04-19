package tailor.api;

import java.util.List;

import tailor.condition.AtomPartition;
import tailor.experiment.description.GroupDescription;

/**
 * Measurement to make on a list of atoms.
 */
public interface AtomListMeasure {
	
	Measurement measure(AtomPartition atomPartition);

	List<GroupDescription> getGroupDescriptions();

}
