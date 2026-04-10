package tailor.experiment.description;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomAngleCondition;

public class AtomAngleDescription extends AtomTripleDescription {
	
	private final double angle;
	
	public AtomAngleDescription(
			double angle, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, DescriptionPath atomDescriptionC) {
		super(atomDescriptionA, atomDescriptionB, atomDescriptionC);
		this.angle = angle;
	}

	public double getAngle() {
		return angle;
	}

	@Override
	public AtomListCondition createCondition() {
		return new AtomAngleCondition(getAngle());
	}
}
