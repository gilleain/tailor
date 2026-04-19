package tailor.experiment.description.atom;

import tailor.experiment.description.AtomValueRangeDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomDistanceMeasure;

public class AtomDistanceRangeDescription extends AtomValueRangeDescription {
	
	public AtomDistanceRangeDescription(
			double minDistance, double maxDistance, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		super(minDistance, maxDistance, new AtomDistanceMeasure(atomDescriptionA, atomDescriptionB));
	}
}
