package tailor.condition.atom;

import tailor.condition.AtomUpperBoundCondition;
import tailor.description.DescriptionPath;
import tailor.measure.AtomAngleMeasure;

public class AtomAngleCondition extends AtomUpperBoundCondition {
	
	public AtomAngleCondition(double angle, DescriptionPath... descriptionPaths) {
		super(angle, new AtomAngleMeasure(descriptionPaths));
	}
}
