package tailor.experiment.description.atom;

import tailor.experiment.description.AtomUpperBoundDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomDistanceMeasure;

public class AtomDistanceDescription extends AtomUpperBoundDescription {
	
	public AtomDistanceDescription(double distance, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		super(distance, new AtomDistanceMeasure(atomDescriptionA, atomDescriptionB));
	}
}
