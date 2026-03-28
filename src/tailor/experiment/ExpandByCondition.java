package tailor.experiment;

import java.util.ArrayList;
import java.util.List;

import tailor.structure.Atom;

public class ExpandByCondition {
	
	private AtomDistanceCondition condition;	// make more general
	
	private List<Atom> source;	// again, more general

	public ExpandByCondition(AtomDistanceCondition condition, List<Atom> source) {
		this.condition = condition;
		this.source = source;
	}
	
	public List<Atom> run(List<Atom> candidates) { // maybe 'apply' ? and 'candidates'??
		List<Atom> results = new ArrayList<>();	// should be more general datastructure - path?
		for (Atom sourceAtom : source) {
			for (Atom candidateAtom : candidates) {
				if (condition.accept(sourceAtom, candidateAtom)) {
					results.add(candidateAtom);
				}
			}
		}
		return results;
	}
	
	

}
