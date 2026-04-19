package tailor.experiment.condition.atom;

import tailor.experiment.condition.AtomUpperBoundCondition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomAngleMeasure;

public class AtomAngleCondition extends AtomUpperBoundCondition {
	
	public AtomAngleCondition(double angle, DescriptionPath... descriptionPaths) {
		super(angle, new AtomAngleMeasure(descriptionPaths));
	}
}
