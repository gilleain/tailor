package tailor.experiment.condition;

import java.util.logging.Logger;

import tailor.experiment.measure.AtomAngleMeasure;

public class AtomAngleCondition extends AbstractAtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomAngleMeasure atomAngleMeasure;
	
	private double angle;
	
	public AtomAngleCondition(AtomMatcher atomMatcher, double angle) {
		this.atomAngleMeasure = new AtomAngleMeasure(atomMatcher);
		this.angle = angle;
	}
	
	public boolean accept(AtomPartition atoms) {
		double actualAngle = atomAngleMeasure.measure(atoms).getValue();
		logger.fine("Angle " + actualAngle + ((actualAngle < angle)? " < " : " > ") + angle);
		return actualAngle < angle;
	}
}
