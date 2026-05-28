package tailor.description.atom;

import tailor.condition.UpperBoundCondition;
import tailor.description.GroupDescriptionPath;
import tailor.measure.AtomAngleMeasure;

public class AtomAngleDescription extends AtomValueDescription {
	
	public AtomAngleDescription(
			double angle, GroupDescriptionPath atomDescriptionA, GroupDescriptionPath atomDescriptionB, GroupDescriptionPath atomDescriptionC) {
		super(new UpperBoundCondition(angle), 
			  new AtomAngleMeasure(atomDescriptionA, atomDescriptionB, atomDescriptionC));
	}
	
}
