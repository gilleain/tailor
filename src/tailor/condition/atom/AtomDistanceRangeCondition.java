package tailor.condition.atom;

import tailor.condition.AtomRangeCondition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomDistanceMeasure;

public class AtomDistanceRangeCondition extends AtomRangeCondition {
	
	private final String name;
	
	public AtomDistanceRangeCondition(double minDistance, double maxDistance, DescriptionPath... paths) {
		this("", minDistance, maxDistance, paths);
	}
	
	public AtomDistanceRangeCondition(String name, double minDistance, double maxDistance, DescriptionPath... paths) {
		super(minDistance, maxDistance, new AtomDistanceMeasure(paths));
		this.name = name;
	}

}
