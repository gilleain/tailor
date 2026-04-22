package tailor.description.atom;

import tailor.description.AtomUpperBoundDescription;
import tailor.description.DescriptionPath;
import tailor.measure.AtomAngleMeasure;

public class AtomAngleDescription extends AtomUpperBoundDescription {
	
	public AtomAngleDescription(
			double angle, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, DescriptionPath atomDescriptionC) {
		super(angle, new AtomAngleMeasure(atomDescriptionA, atomDescriptionB, atomDescriptionC));
	}
	
}
