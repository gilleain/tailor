package tailor.experiment.description.atom;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.condition.AtomDistanceCondition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomDistanceMeasure;

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
		return new AtomDistanceCondition(createMatcher(), getDistance());
	}
	
	@Override
	public AtomListMeasure createMeasure() {
		return new AtomDistanceMeasure(createMatcher());
	}
}
