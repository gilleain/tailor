package tailor.experiment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tailor.structure.Atom;

public class AtomPipe implements Source<Atom>, Sink<Atom> {
	
	private Iterator<Atom> atomIterator;	// TODO - convert to true stream
	private List<Atom> atoms;
	
	public AtomPipe() {
		this.atoms = new ArrayList<>();
		this.atomIterator = null;
	}

	@Override
	public int getArity() {
		return 1;
	}

	@Override
	public Atom getNext() {
		return atomIterator.next();
	}

	@Override
	public boolean hasNext() {
		if (atomIterator == null) {
			atomIterator = atoms.iterator();
		}
	    return atomIterator.hasNext();
	}

	@Override
	public void put(Atom item) {
		atoms.add(item);
	}

}
