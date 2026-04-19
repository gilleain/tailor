package tailor.api;

import tailor.condition.AtomPartition;

/**
 * Conditions on a list of atoms.
 */
public interface AtomListCondition {
	
	
	/**
	 * Check if this atom partition is accepted by this condition.
	 * 
	 * @param atomPartition
	 * @return true if accepted
	 */
	boolean accept(AtomPartition atomPartition);

}
