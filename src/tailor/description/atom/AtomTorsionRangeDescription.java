package tailor.description.atom;

import tailor.description.AtomDescription;
import tailor.description.AtomValueRangeDescription;
import tailor.description.DescriptionPath;
import tailor.measure.AtomTorsionMeasure;

public class AtomTorsionRangeDescription extends AtomValueRangeDescription {
	
	private DescriptionPath atomDescriptionA;
	
	private DescriptionPath atomDescriptionD;
	
	public AtomTorsionRangeDescription(
			double minAngle, double maxAngle,
			DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, 
			DescriptionPath atomDescriptionC, DescriptionPath atomDescriptionD) {
		this("", minAngle, maxAngle, atomDescriptionA, atomDescriptionB, atomDescriptionC, atomDescriptionD);
	}

	public AtomTorsionRangeDescription(String name, double minAngle, double maxAngle,
			DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, 
			DescriptionPath atomDescriptionC, DescriptionPath atomDescriptionD) {
		super(name, minAngle, maxAngle, 
				new AtomTorsionMeasure(name, atomDescriptionA, atomDescriptionB, atomDescriptionC, atomDescriptionD));
		this.atomDescriptionA = atomDescriptionA;
		this.atomDescriptionD = atomDescriptionD;
	}

	public AtomDescription getFirstAtomDescription() {
		return atomDescriptionA.getAtomDescription();
	}
	
	public AtomDescription getLastAtomDescription() {
		return atomDescriptionD.getAtomDescription();
	}
}
