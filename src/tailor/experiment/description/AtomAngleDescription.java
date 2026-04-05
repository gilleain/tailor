package tailor.experiment.description;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.AtomListDescription;
import tailor.experiment.condition.AtomAngleCondition;
import tailor.experiment.condition.AtomMatcher;

public class AtomAngleDescription implements AtomListDescription {
	
	private final double angle;
	
	private final DescriptionPath atomDescriptionA;
	
	private final DescriptionPath atomDescriptionB;
	
	private final DescriptionPath atomDescriptionC;

	public AtomAngleDescription(
			double angle, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, DescriptionPath atomDescriptionC) {
		this.angle = angle;
		this.atomDescriptionA = atomDescriptionA;
		this.atomDescriptionB = atomDescriptionB;
		this.atomDescriptionC = atomDescriptionC;
	}

	public double getAngle() {
		return angle;
	}

	public DescriptionPath getAtomDescriptionA() {
		return atomDescriptionA;
	}

	public DescriptionPath getAtomDescriptionB() {
		return atomDescriptionB;
	}

	public DescriptionPath getAtomDescriptionC() {
		return atomDescriptionC;
	}

	@Override
	public boolean isForSameGroup() {
		return atomDescriptionA.getGroupDescription() == atomDescriptionB.getGroupDescription()
				&& atomDescriptionB.getGroupDescription() == atomDescriptionC.getGroupDescription();
	}
	
	@Override
	public GroupDescription getFirstGroupDescription() {
		return atomDescriptionA.getGroupDescription();
	}

	@Override
	public AtomListCondition makeCondition() {
		return new AtomAngleCondition(getAngle());
	}
	
	@Override
	public AtomMatcher createMatcher() {
		List<String> labels = List.of(
				atomDescriptionA.getAtomDescription().getLabel(),
				atomDescriptionB.getAtomDescription().getLabel(),
				atomDescriptionC.getAtomDescription().getLabel());
		return new AtomMatcher(labels);
	}

}
