package tailor.experiment.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import tailor.structure.Atom;

public class AtomMatcher {
	
	private Logger logger = Logger.getLogger(AtomMatcher.class.getName());
	
	public static class Match {
		
		private List<Atom> atoms;
		private boolean isComplete;
		
		public Match(List<Atom> atoms) {
			this.atoms = atoms;
		}
		
		public boolean isComplete() {
			return this.isComplete;
		}
		
		public Match setComplete() {
			this.isComplete = true;
			return this;
		}
		
		public List<Atom> getAtoms() {
			return this.atoms;
		}
		
		public String toString() {
			String atomLabels = "|";
			for (Atom atom : atoms) {
				atomLabels += atom.getName();
				atomLabels += "|";
			}
			return isComplete + atomLabels;
		}
	}
	
	private LabelPartition atomLabels; // TODO - this is implicitly in order - might want to make that explicit
	
	public AtomMatcher(LabelPartition atomLabels) {
		// this is a partition of the atom labels by residue group
		// TODO - ordered parts of the partition ... which is also ordered?
		this.atomLabels = atomLabels;
	}
	
	public Optional<Match> containedIn(AtomPartition other) {
		// check the label partition is contained in the atom partition
		Match match = findMatch(other);
		if (match.isComplete()) {
			logger.info("MATCH " + match + " for " + other);
			return Optional.of(match);
		} else {
			logger.info("No match " + this.atomLabels + " to " + other + " " + match);
			return Optional.empty();
		}
	}
	
	private Match findMatch(AtomPartition resultAtoms) {
		List<Atom> atomMatches = new ArrayList<>();
		for (int partIndex = 0; partIndex < atomLabels.numberOfParts(); partIndex++) {
			List<String> part = atomLabels.getPart(partIndex);
			List<Atom> atomPart = resultAtoms.getPart(partIndex);
			for (String atomLabel : part) {	
				Atom atom = findAtom(atomLabel, atomPart);
				if (atom == null) {
					return new Match(atomMatches);
				} else {
					atomMatches.add(atom);
				}
			}	
		}
		return new Match(atomMatches).setComplete();
	}
	
	private Atom findAtom(String label, List<Atom> atoms) {
		for (Atom atom : atoms) {
			if (atom.getName().equals(label)) {
				return atom;
			}
		}
		return null;
	}
}
