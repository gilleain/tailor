package tailor.experiment.measure;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.condition.AtomMatcher.Match;
import tailor.experiment.condition.AtomPartition;
import tailor.experiment.condition.LabelPartition;
import tailor.experiment.description.DescriptionPath;
import tailor.structure.Atom;

public abstract class AbstractAtomListMeasure implements AtomListMeasure {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomMatcher atomMatcher;
	
	public AbstractAtomListMeasure(AtomMatcher atomMatcher) {
		this.atomMatcher = atomMatcher;
	}
	
	public AbstractAtomListMeasure(DescriptionPath... descriptionPaths) {
		this(new AtomMatcher(LabelPartition.fromDescriptionPaths(descriptionPaths)));
	}
	
	public DoubleMeasurement measure(AtomPartition atomPartition) {
		Optional<Match> match = atomMatcher.containedIn(atomPartition);
		if (match.isPresent()) {
			return measure(match.get().getAtoms());
		} else {
			logger.fine("NO match " + atomMatcher + " to " + atomPartition);
			return new DoubleMeasurement();
		}
	}
	
	protected abstract DoubleMeasurement measure(List<Atom> atoms);

}
