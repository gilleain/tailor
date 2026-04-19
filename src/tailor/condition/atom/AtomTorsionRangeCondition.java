package tailor.condition.atom;

import tailor.condition.AtomRangeCondition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomTorsionMeasure;

public class AtomTorsionRangeCondition extends AtomRangeCondition {
	
	private String name;
	
	public AtomTorsionRangeCondition(double minAngle, double maxAngle, DescriptionPath... descriptionPaths) {
		this("", minAngle, maxAngle, descriptionPaths);
	}
	
	public AtomTorsionRangeCondition(String name, double minAngle, double maxAngle, DescriptionPath... descriptionPaths) {
		super(minAngle, maxAngle, new AtomTorsionMeasure(descriptionPaths));
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}