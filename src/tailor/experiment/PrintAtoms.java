package tailor.experiment;

import tailor.structure.Atom;

public class PrintAtoms implements Sink<Atom> {

	@Override
	public void put(Atom item) {
		System.out.println(item);
	}

}
