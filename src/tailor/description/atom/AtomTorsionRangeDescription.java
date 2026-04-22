package tailor.description.atom;

import tailor.description.AtomValueRangeDescription;
import tailor.description.DescriptionPath;
import tailor.measure.AtomTorsionMeasure;

public class AtomTorsionRangeDescription extends AtomValueRangeDescription {
	
	private String name;
	
	public AtomTorsionRangeDescription(
			double minAngle, double maxAngle,
			DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, 
			DescriptionPath atomDescriptionC, DescriptionPath atomDescriptionD) {
		this("", minAngle, maxAngle, atomDescriptionA, atomDescriptionB, atomDescriptionC, atomDescriptionD);
	}

	public AtomTorsionRangeDescription(String name, double minAngle, double maxAngle,
			DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, 
			DescriptionPath atomDescriptionC, DescriptionPath atomDescriptionD) {
		super(minAngle, maxAngle, 
				new AtomTorsionMeasure(atomDescriptionA, atomDescriptionB, atomDescriptionC, atomDescriptionD));
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
