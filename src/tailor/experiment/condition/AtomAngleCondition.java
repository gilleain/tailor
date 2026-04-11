package tailor.experiment.condition;

import java.util.List;
import java.util.logging.Logger;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.measure.AtomAngleMeasure;
import tailor.structure.Atom;

public class AtomAngleCondition implements AtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomAngleMeasure aam;
	private double angle;
	
	public AtomAngleCondition(double angle) {
		this.angle = angle;
		this.aam = new AtomAngleMeasure();
	}
	
	public boolean accept(Atom a, Atom b, Atom c) {
		double actualAngle = aam.measure(a, b, c);
		logger.fine("Angle " + actualAngle + ((actualAngle < angle)? " < " : " > ") + angle);
		return actualAngle < angle;
	}
	
	public boolean accept(List<Atom> atoms) {
		assert atoms.size() == 3;	// TODO - alternatively return some kind of error condition?
		return accept(atoms.get(0), atoms.get(1), atoms.get(2));
	}

}
