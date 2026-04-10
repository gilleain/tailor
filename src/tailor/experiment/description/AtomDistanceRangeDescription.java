package tailor.experiment.description;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomDistanceRangeCondition;
import tailor.experiment.condition.AtomMatcher;

public class AtomDistanceRangeDescription extends AtomPairDescription {
	
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

	@Override
	public AtomMatcher createMatcher() {
		List<String> labels = List.of(
				getAtomDescriptionA().getAtomDescription().getLabel(),
				getAtomDescriptionB().getAtomDescription().getLabel());
		return new AtomMatcher(labels);
	}

}
