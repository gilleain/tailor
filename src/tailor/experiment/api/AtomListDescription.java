package tailor.experiment.api;

import java.util.List;

import tailor.experiment.description.GroupDescription;

/**
 * Description for a list of atoms that can create conditions and matchers for filtering.
 */
public interface AtomListDescription {

	/**
	 * @return all group descriptions referenced by this description
	 */
	List<GroupDescription> getGroupDescriptions();
	
	/**
	 * @return the corresponding condition for this description
	 */
	AtomListCondition createCondition();
	
	/**
	 * @return convert this description to a measure across the same atoms
	 */
	AtomListMeasure createMeasure();

}
