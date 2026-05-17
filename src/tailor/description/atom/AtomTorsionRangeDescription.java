package tailor.description.atom;

import tailor.description.AtomDescription;
import tailor.description.AtomValueRangeDescription;
import tailor.description.GroupDescriptionPath;
import tailor.measure.AtomTorsionMeasure;

public class AtomTorsionRangeDescription extends AtomValueRangeDescription {
	
	private GroupDescriptionPath atomDescriptionA;
	
	private GroupDescriptionPath atomDescriptionD;
	
	public AtomTorsionRangeDescription(
			double minAngle, double maxAngle,
			GroupDescriptionPath atomDescriptionA, GroupDescriptionPath atomDescriptionB, 
			GroupDescriptionPath atomDescriptionC, GroupDescriptionPath atomDescriptionD) {
		this("", minAngle, maxAngle, atomDescriptionA, atomDescriptionB, atomDescriptionC, atomDescriptionD);
	}

	public AtomTorsionRangeDescription(String name, double minAngle, double maxAngle,
			GroupDescriptionPath atomDescriptionA, GroupDescriptionPath atomDescriptionB, 
			GroupDescriptionPath atomDescriptionC, GroupDescriptionPath atomDescriptionD) {
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
