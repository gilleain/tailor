package tailor.experiment.operator;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.structure.Atom;

public class PrintAtoms implements Sink<Atom>, Operator {

	@Override
	public void put(Atom item) {
		System.out.println(item);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}


}
