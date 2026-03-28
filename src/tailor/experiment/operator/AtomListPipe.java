package tailor.experiment.operator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.structure.Atom;

/**
 * Could be an Atom_Set_Pipe, but the ordering of the returned atoms matters.
 */
public class AtomListPipe implements Source<List<Atom>>, Sink<List<Atom>> {

	private Iterator<List<Atom>> atomListIterator;
	private List<List<Atom>> atomLists;
	
	public AtomListPipe() {
		this.atomLists = new ArrayList<>();
		this.atomListIterator = null;
	}
	
	@Override
	public int getArity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Atom> getNext() {
		return atomListIterator.next();
	}

	@Override
	public boolean hasNext() {
		if (atomListIterator == null) {
			atomListIterator = atomLists.iterator();
		}
		return atomListIterator.hasNext();
	}

	@Override
	public void put(List<Atom> item) {
		atomLists.add(item);
	}

}
