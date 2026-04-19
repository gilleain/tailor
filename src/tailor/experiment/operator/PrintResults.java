package tailor.experiment.operator;

import tailor.api.Operator;
import tailor.api.Sink;
import tailor.experiment.plan.Result;

public class PrintResults extends AbstractOperator implements Sink<Result> {

	@Override
	public void put(Result item) {
		System.out.println(item);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

	@Override
	public String description() {
		return "PrintResults";
	}

	@Override
	public String getSinkId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerSink(Operator operator) {
		// TODO Auto-generated method stub
		
	}
}
