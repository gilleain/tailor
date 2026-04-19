package tailor.experiment.description.atom;

import tailor.experiment.description.AtomValueRangeDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.measure.AtomTorsionMeasure;

public class AtomTorsionRangeDescription extends AtomValueRangeDescription {
	
	public AtomTorsionRangeDescription(
			double minAngle, double maxAngle,
			DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, 
			DescriptionPath atomDescriptionC, DescriptionPath atomDescriptionD) {
		super(minAngle, maxAngle, 
				new AtomTorsionMeasure(atomDescriptionA, atomDescriptionB, atomDescriptionC, atomDescriptionD));
	}
}
