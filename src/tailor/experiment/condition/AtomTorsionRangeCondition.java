package tailor.experiment.condition;

import java.util.List;

import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomTorsionMeasure;

public class AtomTorsionRangeCondition extends AtomRangeCondition {
	
	public AtomTorsionRangeCondition(double minAngle, double maxAngle, List<DescriptionPath> descriptionPaths) {
		super(minAngle, maxAngle, new AtomTorsionMeasure(descriptionPaths));
	}
}