package tailor.experiment.measure;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.condition.AtomMatcher.Match;
import tailor.experiment.condition.AtomPartition;
import tailor.experiment.condition.LabelPartition;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;
import tailor.structure.Atom;

public abstract class AbstractAtomListMeasure implements AtomListMeasure {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomMatcher atomMatcher;
	
	private final List<DescriptionPath> atomDescriptionPaths;
	
	public AbstractAtomListMeasure(DescriptionPath... descriptionPaths) {
		this(Arrays.asList(descriptionPaths));
	}
		
	public AbstractAtomListMeasure(List<DescriptionPath> descriptionPaths) {	
		this.atomDescriptionPaths = descriptionPaths;
		this.atomMatcher = new AtomMatcher(LabelPartition.fromDescriptionPaths(descriptionPaths));
	}
	
	protected abstract DoubleMeasurement measure(List<Atom> atoms);
	
	public DoubleMeasurement measure(AtomPartition atomPartition) {
		Optional<Match> match = atomMatcher.containedIn(atomPartition);
		if (match.isPresent()) {
			return measure(match.get().getAtoms());
		} else {
			logger.fine("NO match " + atomMatcher + " to " + atomPartition);
			return new DoubleMeasurement();
		}
	}
	
	public List<GroupDescription> getGroupDescriptions() {
		return atomDescriptionPaths.stream().map(DescriptionPath::getGroupDescription).toList();
	}

}
