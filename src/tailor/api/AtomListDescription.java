package tailor.api;

import java.util.List;

import tailor.description.GroupDescription;
import tailor.partition.AtomPartition;

/**
 * Description for a list of atoms that can create conditions and matchers for filtering.
 */
public interface AtomListDescription {

	/**
	 * @return all group descriptions referenced by this description
	 */
	List<GroupDescription> getGroupDescriptions();
	
	/**
	 * @return convert this description to a measure across the same atoms
	 */
	AtomListMeasure createMeasure();
	
	/**
	 * 
	 * @return the result of applying this description to the atoms
	 */
	boolean apply(AtomPartition atomPartition);

}
