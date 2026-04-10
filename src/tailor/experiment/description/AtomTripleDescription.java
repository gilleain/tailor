package tailor.experiment.description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tailor.experiment.api.AtomListDescription;
import tailor.experiment.condition.AtomMatcher;

public abstract class AtomTripleDescription implements AtomListDescription {
	
	
	private final DescriptionPath atomDescriptionA;
	
	private final DescriptionPath atomDescriptionB;
	
	private final DescriptionPath atomDescriptionC;

	public AtomTripleDescription(DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB, DescriptionPath atomDescriptionC) {
		this.atomDescriptionA = atomDescriptionA;
		this.atomDescriptionB = atomDescriptionB;
		this.atomDescriptionC = atomDescriptionC;
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
	public List<GroupDescription> getGroupDescriptions() {
		return List.of(
				atomDescriptionA.getGroupDescription(),
				atomDescriptionB.getGroupDescription(),
				atomDescriptionC.getGroupDescription());
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
	public AtomMatcher createMatcher() {
		List<List<String>> atomLabelPartition = new ArrayList<>();
		
		// TODO - better
		if (isForSameGroup()) {
			// [A,B,C]
			atomLabelPartition.add(labels(atomDescriptionA, atomDescriptionB, atomDescriptionC));
		} else {
			if (atomDescriptionA.getGroupDescription() == atomDescriptionB.getGroupDescription()) {
				// [A,B|C]
				atomLabelPartition.add(labels(atomDescriptionA, atomDescriptionB));
				atomLabelPartition.add(labels(atomDescriptionC));
			} else if (atomDescriptionB.getGroupDescription() == atomDescriptionC.getGroupDescription()) {
				// [A|B,C]
				atomLabelPartition.add(labels(atomDescriptionA));
				atomLabelPartition.add(labels(atomDescriptionB, atomDescriptionC));
			} else {
				// all distinct [A|B|C]
				atomLabelPartition.add(labels(atomDescriptionA));
				atomLabelPartition.add(labels(atomDescriptionB));
				atomLabelPartition.add(labels(atomDescriptionC));
			}
		}
		return new AtomMatcher(atomLabelPartition);
	}
	
	private List<String> labels(DescriptionPath... paths) {
		return Arrays.asList(paths).stream()
				.map(DescriptionPath::getAtomDescription)
				.map(AtomDescription::getLabel)
				.toList();
	}
}
