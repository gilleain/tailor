package tops.translation.experimental;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tailor.engine.plan.Result;


public class Pipe {
	
	private Iterator<Result> resultIterator;
	
	private List<Result> results;
	
	// The id of the operator that is the source
	private String sourceId;
		
	// The id of the operator that is the sink
	private String sinkId;
	
	public Pipe() {
		this.results = new ArrayList<>();
		this.resultIterator = null;
	}

	public Result getNext() {
		return resultIterator.next();
	}
	
	public boolean hasNext() {
		if (resultIterator == null) {
			resultIterator = results.iterator();
		}
		return resultIterator.hasNext();
	}
	
	public String getSourceId() {
		return this.sourceId;
	}

	public void registerSource(Operator operator) {
		this.sourceId = operator.getId();
	}

	public String getSinkId() {
		return this.sinkId;
	}

	public void registerSink(Operator operator) {
		this.sinkId = operator.getId();
	}

	public void put(Result result) {
		this.results.add(result);
	}
}
