package tailor.description.atom;

import tailor.description.AtomValueRangeDescription;
import tailor.description.DescriptionPath;
import tailor.measure.AtomDistanceMeasure;

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
	
	public String getName() {
		return this.name;
	}
}
