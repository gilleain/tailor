package tailor.experiment.condition;

import java.util.logging.Logger;

import tailor.experiment.measure.AtomAngleMeasure;

public class AtomAngleRangeCondition extends AbstractAtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomAngleMeasure atomAngleMeasure;
	
	private double minAngle;
	
	private double maxAngle;
	
	public AtomAngleRangeCondition(AtomMatcher atomMatcher, double minAngle, double maxAngle) {
		this.atomAngleMeasure = new AtomAngleMeasure(atomMatcher);
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
	}
	
	public boolean accept(AtomPartition atoms) {
		double actualAngle = atomAngleMeasure.measure(atoms).getValue();
		boolean isInRange = minAngle < actualAngle && actualAngle < maxAngle;
		logger.fine("Angle " + ((isInRange)? "IS" : "NOT") + " between " 
				+ minAngle + op(minAngle, actualAngle) + actualAngle + op(actualAngle, maxAngle) + maxAngle);
		return isInRange;
	}
}
