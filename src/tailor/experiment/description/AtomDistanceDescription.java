package tailor.experiment.description;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomDistanceCondition;
import tailor.experiment.condition.AtomMatcher;

public class AtomDistanceDescription extends AtomPairDescription {
	
	private final double distance;
	
	public AtomDistanceDescription(double distance, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		super(atomDescriptionA, atomDescriptionB);
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}


	@Override
	public AtomListCondition createCondition() {
		return new AtomDistanceCondition(getDistance());
	}

	@Override
	public AtomMatcher createMatcher() {
		List<String> labels = List.of(
				getAtomDescriptionA().getAtomDescription().getLabel(),
				getAtomDescriptionB().getAtomDescription().getLabel());
		return new AtomMatcher(labels);
	}

}
