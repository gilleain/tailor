package tailor.description.atom;

import tailor.condition.UpperBoundCondition;
import tailor.description.AtomValueDescription;
import tailor.description.DescriptionPath;
import tailor.measure.AtomAngleMeasure;

public class AtomAngleDescription extends AtomValueDescription {
	
	public AtomAngleDescription(
			double angle, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, DescriptionPath atomDescriptionC) {
		super(new UpperBoundCondition(angle), 
			  new AtomAngleMeasure(atomDescriptionA, atomDescriptionB, atomDescriptionC));
	}
	
}
