package tailor.engine.operator;

import java.util.List;

import tailor.engine.plan.Result;
import tailor.structure.Atom;

public class ScanAtomResultByLabel extends AbstractPipeableOperator {
	
	// Atom labels to search for // TODO - could be a general condition
	private List<String> atomLabels;
	
	public ScanAtomResultByLabel(List<String> atomLabels) {
		this.atomLabels = atomLabels;
	}
	
	public void run() {
		while (source.hasNext()) {
			Result groupResult = source.getNext();
			// TODO - could defer copy until we know we have a match
			Result newResult = groupResult.copyWithoutAtoms();
			int matchCount = 0;
			for (Atom atom : groupResult.getAtoms())	{ 
				if (atomLabels.contains(atom.getName())) {	
					newResult.addAtom(atom);
					matchCount++;	// hmmm, bag vs set ...
				}
			}
			if (matchCount == atomLabels.size()) {
				sink.put(newResult);
			}
		}
	}

	@Override
	public String description() {
		return "ScanAtoms id:[" + getId() + "] atoms:" + atomLabels;
	}

}
