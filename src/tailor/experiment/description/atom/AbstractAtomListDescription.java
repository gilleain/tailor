package tailor.experiment.description.atom;

import java.util.Arrays;
import java.util.List;

import tailor.experiment.api.AtomListDescription;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.condition.LabelPartition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;

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

	public AtomMatcher createMatcher() {
		return new AtomMatcher(LabelPartition.fromDescriptionPaths(atomDescriptionPaths));
	}
}
