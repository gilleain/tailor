package tailor.experiment.description.atom;

import tailor.experiment.description.AtomUpperBoundDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomAngleMeasure;

public class AtomAngleDescription extends AtomUpperBoundDescription {
	
	public AtomAngleDescription(
			double angle, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, DescriptionPath atomDescriptionC) {
		super(angle, new AtomAngleMeasure(atomDescriptionA, atomDescriptionB, atomDescriptionC));
	}
	
}
