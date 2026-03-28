package tailor.experiment.operator;

import tailor.experiment.api.Sink;
import tailor.structure.Atom;

public class PrintAtoms implements Sink<Atom> {

	@Override
	public void put(Atom item) {
		System.out.println(item);
	}

}
