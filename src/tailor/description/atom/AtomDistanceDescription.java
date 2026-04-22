package tailor.description.atom;

import tailor.description.AtomUpperBoundDescription;
import tailor.description.DescriptionPath;
import tailor.measure.AtomDistanceMeasure;

public class AtomDistanceDescription extends AtomUpperBoundDescription {
	
	public AtomDistanceDescription(double distance, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		super(distance, new AtomDistanceMeasure(atomDescriptionA, atomDescriptionB));
	}
}
