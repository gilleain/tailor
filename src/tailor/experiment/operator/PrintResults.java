package tailor.experiment.operator;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.plan.Result;

public class PrintResults implements Sink<Result>, Operator {

	@Override
	public void put(Result item) {
		System.out.println(item);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}


}
