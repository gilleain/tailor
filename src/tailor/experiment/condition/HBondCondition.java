package tailor.experiment.condition;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.structure.Atom;

/**
 * Hydrogen bond defined by D-H...A-AA :
 * - Distance between H...A less than 2.5 Angstrom
 * - Angle between DHA is +/- 90 and 180 degrees
 */
public class HBondCondition implements AtomListCondition {
	
	private AtomDistanceCondition haDistanceCondition;
	
	private AtomAngleRangeCondition dhaAngleRangeCondition;
	
	private AtomMatcher distanceMatcher;
	
	private AtomMatcher angleMatcher;
	
	public HBondCondition(AtomMatcher distanceMatcher, AtomMatcher angleMatcher,
			double haDistance, double minDHAAngle, double maxDHAAngle) {
		this.haDistanceCondition = new AtomDistanceCondition(distanceMatcher, haDistance);
		this.dhaAngleRangeCondition = new AtomAngleRangeCondition(angleMatcher, minDHAAngle, maxDHAAngle);
	}

	public boolean accept(AtomPartition atoms) {
		return haDistanceCondition.accept(atoms) && dhaAngleRangeCondition.accept(atoms);
	}
	
	@Override
	public boolean accept(List<Atom> atoms) {
		return false;	// TODO
	}

}
