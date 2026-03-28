package tailor.experiment;

import java.util.List;

import tailor.structure.Atom;

public class PrintAtomLists implements Sink<List<Atom>> {

	@Override
	public void put(List<Atom> item) {
		System.out.println(item);
	}

}
