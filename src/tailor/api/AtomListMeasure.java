package tailor.api;

import java.util.List;

import tailor.description.GroupDescription;
import tailor.partition.AtomPartition;

/**
 * Measurement to make on a list of atoms.
 */
public interface AtomListMeasure {
	
	Measurement<Double> measure(AtomPartition atomPartition);

	/**
	 * @return the group descriptions the measure applies to
	 */
	List<GroupDescription> getGroupDescriptions();

	/**
	 * @return a descriptive name for the measure
	 */
	String getName();

}
