package tailor.experiment.description.atom;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomTorsionRangeCondition;
import tailor.experiment.description.DescriptionPath;

public class AtomTorsionRangeDescription extends AbstractAtomListDescription {
	
	private final double minAngle;
	
	private final double maxAngle;
	
	public AtomTorsionRangeDescription(
			double minAngle, double maxAngle,
			DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, 
			DescriptionPath atomDescriptionC, DescriptionPath atomDescriptionD) {
		super(atomDescriptionA, atomDescriptionB, atomDescriptionC, atomDescriptionD);
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
		return new AtomTorsionRangeCondition(createMatcher(), getMinAngle(), getMaxAngle());
	}
}
