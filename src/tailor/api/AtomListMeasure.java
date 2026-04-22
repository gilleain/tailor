package tailor.api;

import java.util.List;

import tailor.condition.AtomPartition;
import tailor.description.GroupDescription;

/**
 * Measurement to make on a list of atoms.
 */
public interface AtomListMeasure {
	
	Measurement measure(AtomPartition atomPartition);

	/**
	 * @return the group descriptions the measure applies to
	 */
	List<GroupDescription> getGroupDescriptions();

	/**
	 * @return a descriptive name for the measure
	 */
	String getName();

}
