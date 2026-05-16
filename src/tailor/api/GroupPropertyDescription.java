package tailor.api;

import tailor.description.GroupDescription;
import tops.translation.model.Group;

/**
 * Description for a property of a group that can create conditions and matchers for filtering.
 */
public interface GroupPropertyDescription {

	/**
	 * @return the group description referenced by this description
	 */
	GroupDescription getGroupDescription();
	
	/**
	 * @return convert this description to a measure on the group
	 */
	GroupPropertyMeasure createMeasure();
	
	/**
	 * 
	 * @return the result of applying this description to the group
	 */
	boolean apply(Group atomPartition);

}
