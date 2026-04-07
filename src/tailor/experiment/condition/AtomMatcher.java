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
		
		public boolean set(int index, String atomLabel, List<Atom> resultAtoms) {
			for (Atom atom : resultAtoms) {
				if (atom.getName().equals(atomLabel)) {
					this.atoms.set(index, atom);
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
	
	private List<String> atomLabels; // TODO - this is implicitly in order - might want to make that explicit
	
	public AtomMatcher(List<String> atomLabels) {
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
		Match match = new Match(atomLabels.size());
		List<Atom> resultAtoms = result.getAtoms();
		for (int labelIndex = 0; labelIndex < atomLabels.size(); labelIndex++) {
			String atomLabel = atomLabels.get(labelIndex);
			// Check that we can find a matching atom in the result atoms
			boolean isSet = match.set(labelIndex, atomLabel, resultAtoms);
			if (!isSet) {
				System.out.println("No match " + match + " for " + result);
				return match;
			}
		}
		return match.setComplete();
	}
}
