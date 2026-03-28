package tailor.experiment.operator;

import java.util.ArrayList;
import java.util.List;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.structure.Atom;
import tailor.structure.Group;

public class ScanAtomListsByLabel implements Operator {
	
	// List of atom labels to search for // TODO - could be general conditions
	private List<String> atomLabels;
	
	// Source to take from
	private Source<Group> source;
	
	private Sink<List<Atom>> atomOutput;
	
	public ScanAtomListsByLabel(List<String> atomLabels, Source<Group> source, Sink<List<Atom>> atomOutput) {
		this.atomLabels = atomLabels;
		this.source = source;
		this.atomOutput = atomOutput;
	}
	
	public void run() {
		while (source.hasNext()) {
			Group group = source.getNext();
			List<Atom> matches = null;
			int numberOfMatches = 0;
			for (Atom atom : group.getAtoms())	{ // could just use group.getAtomByName?
				for (int labelIndex = 0; labelIndex < atomLabels.size(); labelIndex++) {
					String atomLabel = atomLabels.get(labelIndex);
					if (atom.getName().equals(atomLabel)) {	// is this general enough?
						if (matches == null) {
							matches = makeEmptyList(atomLabels.size());
							
						}
						// ensure the ordering of the output list is the same as the label list
						matches.set(labelIndex, atom);
						numberOfMatches++;
					}
				}
			}
			// TODO - do we want to do anything about partial matches?
			if (matches != null && numberOfMatches == atomLabels.size()) {
				atomOutput.put(matches);
			}
		}
	}
	
	private List<Atom> makeEmptyList(int size) {
		List<Atom> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			list.add(null);
		}
		return list;
	}

}
