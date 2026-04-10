package tailor.experiment.condition;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.measure.AtomDistanceMeasure;
import tailor.structure.Atom;

public class AtomDistanceRangeCondition implements AtomListCondition {
	
	private AtomDistanceMeasure adm;
	private double minDistance;
	private double maxDistance;
	
	public AtomDistanceRangeCondition(double minDistance, double maxDistance) {
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.adm = new AtomDistanceMeasure();
	}
	
	public boolean accept(Atom a, Atom b) {
		double actualDistance = adm.measure(a, b);
		boolean isInRange = minDistance < actualDistance && actualDistance < maxDistance;
		System.out.println("Distance " + ((isInRange)? "IS" : "NOT") +" between " 
				+ minDistance + op(minDistance, actualDistance) + actualDistance + op(actualDistance, maxDistance) + maxDistance);
		return isInRange;
	}
	
	private String op(double a, double b) {
		return a < b? " < " : " > ";
	}
	
	public boolean accept(List<Atom> atoms) {
		assert atoms.size() == 2;	// TODO - alternatively return some kind of error condition?
		return accept(atoms.get(0), atoms.get(1));
	}

}
