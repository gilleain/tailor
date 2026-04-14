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
		
		public Match(int size) {
			this.atoms = new ArrayList<>();
			for (int index = 0; index < size; index++) {
				atoms.add(null);
			}	
		}
		
		/**
		 * @param partIndex the index of the part within the partition (equivalent to the group)
		 * @param labelIndex the index of the atomLabel within the part
		 * @param outIndex the final index in the match
		 * @param atomLabel the atom label
		 * @param resultAtomPartition the atoms to match against
		 * @return
		 */
		public boolean set(int labelIndex, int outIndex, String atomLabel, List<Atom> resultAtoms) {
			for (Atom resultAtom : resultAtoms) {
				if (resultAtom.getName().equals(atomLabel)) {
					this.atoms.set(outIndex, resultAtom);
					return true;
				}
			}
			return false;
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
				atomLabels += (atom == null)? "!" : atom.getName();
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
			logger.fine("MATCH " + match + " for " + other);
			return Optional.of(match);
		} else {
			logger.info("No match " + this.atomLabels + " to " + other + " " + match);
			return Optional.empty();
		}
	}
	
	private Match findMatch(AtomPartition resultAtoms) {
		int numberOfAtoms = atomLabels.totalElements();
		Match match = new Match(numberOfAtoms);
		
		int outIndex = 0;
		for (int partIndex = 0; partIndex < atomLabels.numberOfParts(); partIndex++) {
			List<String> part = atomLabels.getPart(partIndex);
			List<Atom> atomPart = resultAtoms.getPart(partIndex);
			for (int labelIndex = 0; labelIndex < part.size(); labelIndex++) {
				String atomLabel = part.get(labelIndex);
				// Check that we can find a matching atom in the result atoms
				boolean isSet = match.set(labelIndex, outIndex, atomLabel, atomPart);
				if (!isSet) {
					return match;
				}
				outIndex++;
			}	
		}
		return match.setComplete();
	}
}
