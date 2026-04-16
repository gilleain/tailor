package tailor.experiment.api;

import java.util.List;

import tailor.experiment.description.GroupDescription;

/**
 * Description for a list of atoms that can create conditions and matchers for filtering.
 */
public interface AtomListDescription {

	/**
	 * @return true if all atom descriptions are in the same group
	 */
	boolean isForSameGroup();

	/**
	 * @return the first group description (if all the same, this will be a representative)
	 */
	GroupDescription getFirstGroupDescription();

	/**
	 * @return all group descriptions referenced by this description
	 */
	List<GroupDescription> getGroupDescriptions();
	
	/**
	 * @return the corresponding condition for this description
	 */
	AtomListCondition createCondition();
	

}
