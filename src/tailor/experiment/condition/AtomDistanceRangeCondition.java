package tailor.experiment.condition;

import java.util.List;

import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomDistanceMeasure;

public class AtomDistanceRangeCondition extends AtomRangeCondition {
	
	public AtomDistanceRangeCondition(double minDistance, double maxDistance, List<DescriptionPath> paths) {
		super(minDistance, maxDistance, new AtomDistanceMeasure(paths));
	}

}
