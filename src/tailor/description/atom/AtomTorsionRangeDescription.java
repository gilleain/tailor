package tailor.description.atom;

import tailor.description.AtomDescription;
import tailor.description.AtomValueRangeDescription;
import tailor.description.DescriptionPath;
import tailor.measure.AtomTorsionMeasure;

public class AtomTorsionRangeDescription extends AtomValueRangeDescription {
	
	private String name;
	
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
		super(minAngle, maxAngle, 
				new AtomTorsionMeasure(atomDescriptionA, atomDescriptionB, atomDescriptionC, atomDescriptionD));
		this.name = name;
		this.atomDescriptionA = atomDescriptionA;
		this.atomDescriptionD = atomDescriptionD;
	}

	public String getName() {
		return this.name;
	}
	
	public AtomDescription getFirstAtomDescription() {
		return atomDescriptionA.getAtomDescription();
	}
	
	public AtomDescription getLastAtomDescription() {
		return atomDescriptionD.getAtomDescription();
	}
}
