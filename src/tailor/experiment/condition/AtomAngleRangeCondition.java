package tailor.experiment.condition;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.measure.AtomAngleMeasure;
import tailor.structure.Atom;

public class AtomAngleRangeCondition implements AtomListCondition {
	
	private AtomAngleMeasure aam;
	private double minAngle;
	private double maxAngle;
	
	public AtomAngleRangeCondition(double minAngle, double maxAngle) {
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
		this.aam = new AtomAngleMeasure();
	}
	
	public boolean accept(Atom a, Atom b, Atom c) {
		double actualAngle = aam.measure(a, b, c);
		System.out.println("Angle between" 
				+ minAngle + op(minAngle, actualAngle) + actualAngle + op(actualAngle, maxAngle) + maxAngle);
		return minAngle < actualAngle && actualAngle < maxAngle;
	}
	
	public boolean accept(List<Atom> atoms) {
		assert atoms.size() == 3;	// TODO - alternatively return some kind of error condition?
		return accept(atoms.get(0), atoms.get(1), atoms.get(2));
	}
	
	private String op(double a, double b) {
		return a < b? " < " : " > ";
	}
}
