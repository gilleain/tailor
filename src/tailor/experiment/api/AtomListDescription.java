package tailor.experiment.api;

import tailor.experiment.condition.AtomMatcher;
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
	 * @return the corresponding condition for this description
	 */
	AtomListCondition makeCondition();
	
	/**
	 * @return an atom matcher for this description
	 */
	AtomMatcher createMatcher();

}
