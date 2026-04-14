package tailor.experiment.condition;

import java.util.List;
import java.util.logging.Logger;

import tailor.experiment.measure.AtomTorsionMeasure;
import tailor.structure.Atom;

public class AtomTorsionRangeCondition extends AbstractAtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomTorsionMeasure atm;
	private double minAngle;
	private double maxAngle;
	
	public AtomTorsionRangeCondition(AtomMatcher atomMatcher, double minAngle, double maxAngle) {
		super(atomMatcher);
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
		this.atm = new AtomTorsionMeasure();
	}

	@Override
	public boolean accept(List<Atom> atoms) {
		assert atoms.size() == 4;	// TODO - alternatively return some kind of error condition?
		return accept(atoms.get(0), atoms.get(1), atoms.get(2), atoms.get(3));
	}
	
	public boolean accept(Atom a, Atom b, Atom c, Atom d) {
		double actualAngle = atm.measure(a, b, c, d);
		boolean isInRange = minAngle < actualAngle && actualAngle < maxAngle;
		logger.info("Angle " + ((isInRange)? "IS" : "NOT") + " between " 
				+ minAngle + op(minAngle, actualAngle) + actualAngle + op(actualAngle, maxAngle) + maxAngle);
		return isInRange;
	}
	
	private String op(double a, double b) {
		return a < b? " < " : " > ";
	}
}