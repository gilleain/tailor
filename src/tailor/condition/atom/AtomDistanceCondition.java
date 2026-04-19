package tailor.condition.atom;

import java.util.List;

import tailor.condition.AtomUpperBoundCondition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomDistanceMeasure;

public class AtomDistanceCondition extends AtomUpperBoundCondition {
	
	public AtomDistanceCondition(double distance, List<DescriptionPath> descriptionPaths) {
		super(distance, new AtomDistanceMeasure(descriptionPaths));
	}
	
}
