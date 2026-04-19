package tailor.experiment.description.atom;

import tailor.experiment.description.AtomValueRangeDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomAngleMeasure;

public class AtomAngleRangeDescription extends AtomValueRangeDescription {
	
	public AtomAngleRangeDescription(
			double minAngle, double maxAngle,
			DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, DescriptionPath atomDescriptionC) {
		super(minAngle, maxAngle, new AtomAngleMeasure(atomDescriptionA, atomDescriptionB, atomDescriptionC));
	}
}
