package tailor.api;

import tailor.description.GroupDescription;
import tops.translation.model.Group;

/**
 * Measurement to make on a group.
 */
public interface GroupPropertyMeasure {
	
	Measurement<String> measure(Group group);

	/**
	 * @return the group description the measure applies to
	 */
	GroupDescription getGroupDescription();

	/**
	 * @return a descriptive name for the measure
	 */
	String getName();

}
