package tailor.condition.atom;

import java.util.List;

import tailor.api.AtomListCondition;
import tailor.condition.AtomPartition;
import tailor.experiment.description.DescriptionPath;

/**
 * Hydrogen bond defined by D-H...A-AA :
 * - Distance between H...A less than 2.5 Angstrom
 * - Angle between DHA is +/- 90 and 180 degrees
 */
public class HBondCondition implements AtomListCondition {
	
	private AtomDistanceCondition haDistanceCondition;
	
	private AtomAngleRangeCondition dhaAngleRangeCondition;
	
	public HBondCondition(double haDistance, double minDHAAngle, double maxDHAAngle,
			List<DescriptionPath> distancePaths, List<DescriptionPath> anglePaths) {
		this.haDistanceCondition = new AtomDistanceCondition(haDistance, distancePaths);
		this.dhaAngleRangeCondition = new AtomAngleRangeCondition(minDHAAngle, maxDHAAngle, anglePaths);
	}

	public boolean accept(AtomPartition atoms) {
		return haDistanceCondition.accept(atoms) && dhaAngleRangeCondition.accept(atoms);
	}
}
