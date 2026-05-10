package tailor.description.atom;

import tailor.condition.UpperBoundCondition;
import tailor.description.AtomValueDescription;
import tailor.description.DescriptionPath;
import tailor.measure.AtomDistanceMeasure;

public class AtomDistanceDescription extends AtomValueDescription {
	
	public AtomDistanceDescription(double distance, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		super(new UpperBoundCondition(distance), 
			  new AtomDistanceMeasure(atomDescriptionA, atomDescriptionB));
	}
}
