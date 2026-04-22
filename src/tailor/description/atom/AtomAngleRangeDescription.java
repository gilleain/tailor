package tailor.description.atom;

import tailor.description.AtomValueRangeDescription;
import tailor.description.DescriptionPath;
import tailor.measure.AtomAngleMeasure;

public class AtomAngleRangeDescription extends AtomValueRangeDescription {
	
	public AtomAngleRangeDescription(
			double minAngle, double maxAngle,
			DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, DescriptionPath atomDescriptionC) {
		super(minAngle, maxAngle, new AtomAngleMeasure(atomDescriptionA, atomDescriptionB, atomDescriptionC));
	}
}
