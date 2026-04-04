package tailor.experiment.condition;

import java.util.ArrayList;
import java.util.List;

import tailor.experiment.plan.Result;
import tailor.structure.Atom;

public class AtomMatcher {
	
	private List<String> atomLabels; // TODO - this is implicitly in order - might want to make that explicit
	
	public AtomMatcher(List<String> atomLabels) {
		this.atomLabels = atomLabels;
	}

	public List<List<Atom>> extract(Result result) {
		List<List<Atom>> matches = new ArrayList<>();
		// TODO - need a combinatorial approach here
		List<Atom> match = findMatch(result);
		if (match != null) {
			matches.add(match);
		}
		
		return matches;
	}
	
	private List<Atom> findMatch(Result result) {
		List<Atom> match = blank(atomLabels.size());
		for (Atom atom : result.getAtoms()) {
			if (set(atom, match)) {
				continue;
			} else {
				System.out.println("No match " + match + " for " + result);
				return null;
			}
		}
		return match;
	}
	
	private boolean set(Atom atom, List<Atom> match) {
		int index = 0;
		for (String label : atomLabels) {
			if (atom.getName().equals(label)) {
				match.set(index, atom);
				return true;
			}
			index++;
		}
		return false;
	}
	
	private List<Atom> blank(int size) {
		List<Atom> match = new ArrayList<>();
		for (int index = 0; index < size; index++) {
			match.add(null);
		}
		return match;
	}
}
