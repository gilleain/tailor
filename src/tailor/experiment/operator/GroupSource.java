package tailor.experiment.operator;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.plan.Result;
import tailor.structure.Chain;
import tailor.structure.Group;

public class GroupSource implements Operator {
	
	private Chain chain;
	
	private Sink<Result> output;
	
	public GroupSource(Chain chain, Sink<Result> output) {
		this.chain = chain;
		this.output = output;
	}

	@Override
	public void run() {
		for (Group group : chain.getGroups()) {
			output.put(new Result(chain, group));
		}
		
	}

}
