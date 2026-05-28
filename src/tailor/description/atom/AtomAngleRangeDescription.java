package tailor.description.atom;

import tailor.description.GroupDescriptionPath;
import tailor.measure.AtomAngleMeasure;

public class AtomAngleRangeDescription extends AtomValueRangeDescription {
	
	public AtomAngleRangeDescription(
			double minAngle, double maxAngle,
			GroupDescriptionPath atomDescriptionA, GroupDescriptionPath atomDescriptionB, GroupDescriptionPath atomDescriptionC) {
		super(minAngle, maxAngle, new AtomAngleMeasure(atomDescriptionA, atomDescriptionB, atomDescriptionC));
	}
}
