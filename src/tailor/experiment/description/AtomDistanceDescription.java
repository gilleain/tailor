package tailor.experiment.description;

import java.util.List;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.AtomListDescription;
import tailor.experiment.condition.AtomDistanceCondition;
import tailor.experiment.condition.AtomMatcher;

public class AtomDistanceDescription implements AtomListDescription {
	
	private final double distance;
	
	private final DescriptionPath atomDescriptionA;
	
	private final DescriptionPath atomDescriptionB;

	public AtomDistanceDescription(double distance, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		this.distance = distance;
		this.atomDescriptionA = atomDescriptionA;
		this.atomDescriptionB = atomDescriptionB;
	}

	public double getDistance() {
		return distance;
	}

	public DescriptionPath getAtomDescriptionA() {
		return atomDescriptionA;
	}

	public DescriptionPath getAtomDescriptionB() {
		return atomDescriptionB;
	}

	@Override
	public List<GroupDescription> getGroupDescriptions() {
		return List.of(atomDescriptionA.getGroupDescription(), atomDescriptionB.getGroupDescription());
	}

	@Override
	public boolean isForSameGroup() {
		return atomDescriptionA.getGroupDescription() == atomDescriptionB.getGroupDescription();
	}

	@Override
	public GroupDescription getFirstGroupDescription() {
		return atomDescriptionA.getGroupDescription();
	}

	@Override
	public AtomListCondition makeCondition() {
		return new AtomDistanceCondition(getDistance());
	}

	@Override
	public AtomMatcher createMatcher() {
		List<String> labels = List.of(
				atomDescriptionA.getAtomDescription().getLabel(),
				atomDescriptionB.getAtomDescription().getLabel());
		return new AtomMatcher(labels);
	}

}
