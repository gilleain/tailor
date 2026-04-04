package tailor.experiment.operator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.plan.Result;

public class ResultPipe implements Source<Result>, Sink<Result> {
	
	private Iterator<Result> resultIterator;	// TODO - convert to true stream
	private List<Result> results;
	
	public ResultPipe() {
		this.results = new ArrayList<>();
		this.resultIterator = null;
	}


	@Override
	public void put(Result item) {
		results.add(item);
	}

	@Override
	public int getArity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Result getNext() {
		return resultIterator.next();
	}

	@Override
	public boolean hasNext() {
		if (resultIterator == null) {
			resultIterator = results.iterator();
		}
		return resultIterator.hasNext();
	}

}
