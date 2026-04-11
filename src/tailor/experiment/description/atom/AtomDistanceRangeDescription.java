package tailor.experiment.description.atom;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomDistanceRangeCondition;
import tailor.experiment.description.DescriptionPath;

public class AtomDistanceRangeDescription extends AbstractAtomListDescription {
	
	private final double minDistance;
	
	private final double maxDistance;
	
	public AtomDistanceRangeDescription(
			double minDistance, double maxDistance, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		super(atomDescriptionA, atomDescriptionB);
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
	}

	public double getMinDistance() {
		return minDistance;
	}
	
	public double getMaxDistance() {
		return maxDistance;
	}

	@Override
	public AtomListCondition createCondition() {
		return new AtomDistanceRangeCondition(getMinDistance(), getMaxDistance());
	}
}
