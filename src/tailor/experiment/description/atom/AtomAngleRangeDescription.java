package tailor.experiment.description.atom;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomAngleRangeCondition;
import tailor.experiment.description.DescriptionPath;

public class AtomAngleRangeDescription extends AbstractAtomListDescription {
	
	private final double minAngle;
	
	private final double maxAngle;
	
	public AtomAngleRangeDescription(
			double minAngle, double maxAngle,
			DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, DescriptionPath atomDescriptionC) {
		super(atomDescriptionA, atomDescriptionB, atomDescriptionC);
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
	}

	public double getMinAngle() {
		return minAngle;
	}
	
	public double getMaxAngle() {
		return maxAngle;
	}

	@Override
	public AtomListCondition createCondition() {
		return new AtomAngleRangeCondition(createMatcher(), getMinAngle(), getMaxAngle());
	}
}
