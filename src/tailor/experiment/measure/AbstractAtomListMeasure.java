package tailor.experiment.measure;

import java.util.Optional;

import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.condition.AtomMatcher.Match;
import tailor.experiment.condition.AtomPartition;

public abstract class AbstractAtomListMeasure implements AtomListMeasure {
	
	private AtomMatcher atomMatcher;
	
	public AbstractAtomListMeasure(AtomMatcher atomMatcher) {
		this.atomMatcher = atomMatcher;
	}
	
	public double measure(AtomPartition atomPartition) {
		Optional<Match> match = atomMatcher.containedIn(atomPartition);
		if (match.isPresent()) {
			return measure(match.get().getAtoms());
		} else {
			return -1;	// TODO
		}
	}

}
