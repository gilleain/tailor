package tailor.experiment.description.atom;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomAngleCondition;
import tailor.experiment.description.DescriptionPath;

public class AtomAngleDescription extends AbstractAtomListDescription {
	
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
		return new AtomAngleCondition(createMatcher(), getAngle());
	}
	
}
