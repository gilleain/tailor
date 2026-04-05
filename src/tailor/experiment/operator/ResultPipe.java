package tailor.experiment.operator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.plan.Result;

public class ResultPipe implements Source<Result>, Sink<Result> {
	
	private Iterator<Result> resultIterator;	// TODO - convert to true stream
	private List<Result> results;
	
	// The id of the operator that is the source
	private String sourceId;
	
	// The id of the operator that is the sink
	private String sinkId;
	
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


	@Override
	public String getSourceId() {
		return this.sourceId;
	}


	@Override
	public void registerSource(Operator operator) {
		this.sourceId = operator.getId();
	}


	@Override
	public String getSinkId() {
		return this.sinkId;
	}


	@Override
	public void registerSink(Operator operator) {
		this.sinkId = operator.getId();
	}

}
