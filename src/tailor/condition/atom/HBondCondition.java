package tailor.condition.atom;

import java.util.List;

import tailor.api.AtomListCondition;
import tailor.condition.AtomPartition;
import tailor.description.DescriptionPath;

/**
 * Hydrogen bond defined by D-H...A-AA :
 * - Distance between H...A less than 2.5 Angstrom
 * - Angle between DHA is +/- 90 and 180 degrees
 */
public class HBondCondition implements AtomListCondition {
	
	private AtomDistanceCondition haDistanceCondition;
	
	private AtomAngleRangeCondition daaAngleRangeCondition;
	
	public HBondCondition(double haDistance, double minDAAAngle, double maxDAAAngle,
			List<DescriptionPath> distancePaths, List<DescriptionPath> anglePaths) {
		this.haDistanceCondition = new AtomDistanceCondition(haDistance, distancePaths);
		this.daaAngleRangeCondition = new AtomAngleRangeCondition(minDAAAngle, maxDAAAngle, anglePaths);
	}

	public boolean accept(AtomPartition atoms) {
		return haDistanceCondition.accept(atoms) && daaAngleRangeCondition.accept(atoms);
	}
}
