package tailor.experiment.condition;

import java.util.logging.Logger;

import tailor.experiment.measure.AtomDistanceMeasure;

public class AtomDistanceRangeCondition extends AbstractAtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomDistanceMeasure atomDistanceMeasure;
	
	private double minDistance;
	private double maxDistance;
	
	public AtomDistanceRangeCondition(AtomMatcher atomMatcher, double minDistance, double maxDistance) {
		this.atomDistanceMeasure = new AtomDistanceMeasure(atomMatcher);
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
	}
	
	public boolean accept(AtomPartition atoms) {
		double actualDistance = atomDistanceMeasure.measure(atoms).getValue();
		boolean isInRange = minDistance < actualDistance && actualDistance < maxDistance;
		logger.fine("Distance " + ((isInRange)? "IS" : "NOT") +" between " 
				+ minDistance + op(minDistance, actualDistance) + actualDistance + op(actualDistance, maxDistance) + maxDistance);
		return isInRange;
	}

}
