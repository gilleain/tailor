package tailor.experiment.condition;

import java.util.logging.Logger;

import tailor.experiment.measure.AtomTorsionMeasure;

public class AtomTorsionRangeCondition extends AbstractAtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomTorsionMeasure atomTorsionMeasure;
	
	private double minAngle;
	
	private double maxAngle;
	
	public AtomTorsionRangeCondition(AtomMatcher atomMatcher, double minAngle, double maxAngle) {
		this.atomTorsionMeasure = new AtomTorsionMeasure(atomMatcher);
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
	}
	
	public boolean accept(AtomPartition atoms) {
		double actualAngle = atomTorsionMeasure.measure(atoms).getValue();
		boolean isInRange = minAngle < actualAngle && actualAngle < maxAngle;
		logger.info("Angle " + ((isInRange)? "IS" : "NOT") + " between " 
				+ minAngle + op(minAngle, actualAngle) + actualAngle + op(actualAngle, maxAngle) + maxAngle);
		return isInRange;
	}
}