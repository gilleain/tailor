package tailor.experiment.condition;

import java.util.logging.Logger;

import tailor.experiment.measure.AtomDistanceMeasure;

public class AtomDistanceCondition extends AbstractAtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomDistanceMeasure atomDistanceMeasure;
	private double distance;
	
	public AtomDistanceCondition(AtomMatcher atomMatcher, double distance) {
		this.atomDistanceMeasure = new AtomDistanceMeasure(atomMatcher);
		this.distance = distance;
	}
	
	public boolean accept(AtomPartition atoms) {
		double actualDistance = atomDistanceMeasure.measure(atoms).getValue();
		logger.fine("Distance " + actualDistance + ((actualDistance < distance)? " < " : " > ") + distance);
		return actualDistance < distance;
	}
	
}
