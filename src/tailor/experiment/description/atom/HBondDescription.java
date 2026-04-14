package tailor.experiment.description.atom;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.HBondCondition;
import tailor.experiment.description.DescriptionPath;

public class HBondDescription extends AbstractAtomListDescription {
	
	private final double haDistance;
	
	private final double minDHAAngle;

	private final double maxDHAAngle;
	
	public HBondDescription(double haDistance, double minDHAAngle, double maxDHAAngle, DescriptionPath... atomDescriptionPaths) {
		super(atomDescriptionPaths);
		this.haDistance = haDistance;
		this.minDHAAngle = minDHAAngle;
		this.maxDHAAngle = maxDHAAngle;
	}

	@Override
	public AtomListCondition createCondition() {
		return new HBondCondition(createMatcher(), createMatcher(), haDistance, minDHAAngle, maxDHAAngle);
	}

}
