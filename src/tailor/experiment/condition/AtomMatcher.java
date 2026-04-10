package tailor.experiment.condition;

import java.util.ArrayList;
import java.util.List;

import tailor.experiment.plan.Result;
import tailor.structure.Atom;

public class AtomMatcher {
	
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
		public boolean set(int partIndex, int labelIndex, int outIndex, String atomLabel, List<List<Atom>> resultAtomPartition) {
			List<Atom> resultAtoms = resultAtomPartition.get(partIndex);
			for (Atom atom : resultAtoms) {
				if (atom.getName().equals(atomLabel)) {
					this.atoms.set(outIndex, atom);
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
	
	private List<List<String>> atomLabels; // TODO - this is implicitly in order - might want to make that explicit
	
	public AtomMatcher(List<List<String>> atomLabels) {
		// this is a partition of the atom labels by residue group
		// TODO - ordered parts of the partition ... which is also ordered?
		this.atomLabels = atomLabels;
	}

	public List<Match> extract(Result result) {
		List<Match> matches = new ArrayList<>();
		// TODO - need a combinatorial approach here
		Match match = findMatch(result);
		if (match.isComplete()) {
			System.out.println("MATCH " + match + " for " + result);
			matches.add(match);
		}
		
		return matches;
	}
	
	private Match findMatch(Result result) {
		int numberOfAtoms = atomLabels.stream().map(List::size).reduce(0, Integer::sum);
		Match match = new Match(numberOfAtoms);
		List<List<Atom>> resultAtoms = result.getAtomPartition();
		
		int outIndex = 0;
		for (int partIndex = 0; partIndex < atomLabels.size(); partIndex++) {
			List<String> part = atomLabels.get(partIndex);
			for (int labelIndex = 0; labelIndex < part.size(); labelIndex++) {
				String atomLabel = part.get(labelIndex);
				// Check that we can find a matching atom in the result atoms
				boolean isSet = match.set(partIndex, labelIndex, outIndex, atomLabel, resultAtoms);
				if (!isSet) {
					System.out.println("No match " + match + " for " + result);
					return match;
				}
				outIndex++;
			}	
		}
		
		
		return match.setComplete();
	}
}
