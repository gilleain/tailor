package tailor.experiment.operator;

import java.util.List;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.structure.Atom;

public class PrintAtomLists implements Sink<List<Atom>>, Operator {

	@Override
	public void put(List<Atom> item) {
		System.out.println(item);	// TODO - should this buffer?
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

}
