package tailor.experiment.description.atom;

import tailor.experiment.description.AtomValueRangeDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomDistanceMeasure;

public class AtomDistanceRangeDescription extends AtomValueRangeDescription {
	
	private String name;
	
	public AtomDistanceRangeDescription(
			double minDistance, double maxDistance, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		this("", minDistance, maxDistance, atomDescriptionA, atomDescriptionB);
	}
	
	public AtomDistanceRangeDescription(
			String name, double minDistance, double maxDistance, 
			DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		super(minDistance, maxDistance, new AtomDistanceMeasure(atomDescriptionA, atomDescriptionB));
		this.name = name;
	}
}
