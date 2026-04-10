package tailor.experiment.description;

import java.util.Arrays;
import java.util.List;

import tailor.experiment.api.AtomListDescription;
import tailor.experiment.condition.AtomMatcher;

/**
 * Abstract description of a pair of atoms.
 */
public abstract class AtomPairDescription implements AtomListDescription {
	
	

	private final DescriptionPath atomDescriptionA;
	
	private final DescriptionPath atomDescriptionB;

	public AtomPairDescription(DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		this.atomDescriptionA = atomDescriptionA;
		this.atomDescriptionB = atomDescriptionB;
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
	public AtomMatcher createMatcher() {
		if (getAtomDescriptionA().getGroupDescription() == getAtomDescriptionB().getGroupDescription()) {
			return new AtomMatcher(List.of(labels(getAtomDescriptionA(), getAtomDescriptionB())));
		} else {
			return new AtomMatcher(List.of(labels(getAtomDescriptionA()), labels(getAtomDescriptionB())));
		}
	}

	private List<String> labels(DescriptionPath... paths) {
		return Arrays.asList(paths).stream()
				.map(DescriptionPath::getAtomDescription)
				.map(AtomDescription::getLabel)
				.toList();
	}
}
