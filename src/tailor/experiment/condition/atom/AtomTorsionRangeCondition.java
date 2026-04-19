package tailor.experiment.condition.atom;

import java.util.List;

import tailor.experiment.condition.AtomRangeCondition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomTorsionMeasure;

public class AtomTorsionRangeCondition extends AtomRangeCondition {
	
	public AtomTorsionRangeCondition(double minAngle, double maxAngle, List<DescriptionPath> descriptionPaths) {
		super(minAngle, maxAngle, new AtomTorsionMeasure(descriptionPaths));
	}
}