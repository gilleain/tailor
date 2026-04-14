package tailor.experiment.condition;

import java.util.Optional;
import java.util.logging.Logger;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.condition.AtomMatcher.Match;

public abstract class AbstractAtomListCondition implements AtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomMatcher atomMatcher;
	
	public AbstractAtomListCondition(AtomMatcher atomMatcher) {
		this.atomMatcher = atomMatcher;
	}
	
	public boolean accept(AtomPartition atomPartition) {
		Optional<Match> match = atomMatcher.containedIn(atomPartition);
		logger.fine("Checking " + match + " to " + atomPartition);
		if (match.isPresent() && accept(match.get().getAtoms())) {
			logger.fine(" Match");
			return true;
		} else {
			logger.fine(" NO Match " + atomPartition);
			return false;
		}
	}

}
