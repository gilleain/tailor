package tailor.condition.atom;

import java.util.List;

import tailor.condition.AtomRangeCondition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomAngleMeasure;

public class AtomAngleRangeCondition extends AtomRangeCondition {
	
	public AtomAngleRangeCondition(double minValue, double maxValue, List<DescriptionPath> paths) {
		super(minValue, maxValue, new AtomAngleMeasure(paths));
	}
	
}
