package tailor.experiment.description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tailor.experiment.api.AtomListDescription;
import tailor.experiment.condition.AtomMatcher;

/**
 * Abstract description of a list of atoms.
 */
public abstract class AbstractAtomListDescription implements AtomListDescription {
	
	private final List<DescriptionPath> atomDescriptionPaths;

	public AbstractAtomListDescription(DescriptionPath... atomDescriptionPaths) {
		this.atomDescriptionPaths = Arrays.asList(atomDescriptionPaths);
	}

	public DescriptionPath getAtomDescriptionPath(int index) {
		return atomDescriptionPaths.get(index);
	}


	@Override
	public List<GroupDescription> getGroupDescriptions() {
		return atomDescriptionPaths.stream().map(DescriptionPath::getGroupDescription).toList();
	}

	@Override
	public boolean isForSameGroup() {
		GroupDescription first = getFirstGroupDescription();
		for (DescriptionPath descriptionPath : atomDescriptionPaths.subList(1, atomDescriptionPaths.size())) {
			if (descriptionPath.getGroupDescription() != first) {
				return false;
			}
		}
		return true;
	}

	@Override
	public GroupDescription getFirstGroupDescription() {
		return atomDescriptionPaths.get(0).getGroupDescription();
	}
	
	@Override
	public AtomMatcher createMatcher() {
		Map<GroupDescription, List<String>> partition = new HashMap<>();
		for (DescriptionPath descriptionPath : atomDescriptionPaths) {
			GroupDescription groupDescription = descriptionPath.getGroupDescription();
			List<String> atomLabels;
			if (partition.containsKey(groupDescription)) {
				atomLabels = partition.get(groupDescription);
			} else {
				atomLabels = new ArrayList<>();
				partition.put(groupDescription, atomLabels);
			}
			atomLabels.add(descriptionPath.getAtomDescription().getLabel());
		}
		
		List<List<String>> labelPartition = partition.values().stream().toList();
		return new AtomMatcher(labelPartition);
		
	}
}
