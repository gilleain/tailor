package tailor.experiment.description;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomMatcher;

public interface AtomSetDescription {
	
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
