package tailor.experiment.condition;

import java.util.List;

import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomAngleMeasure;

public class AtomAngleRangeCondition extends AtomRangeCondition {
	
	public AtomAngleRangeCondition(double minValue, double maxValue, List<DescriptionPath> paths) {
		super(minValue, maxValue, new AtomAngleMeasure(paths));
	}
	
}
