package tailor.experiment.description;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomDistanceCondition;

public class AtomDistanceDescription extends AbstractAtomListDescription {
	
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
}
