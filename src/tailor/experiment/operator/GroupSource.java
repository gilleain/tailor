package tailor.experiment.operator;

import java.util.List;

import tailor.api.Operator;
import tailor.api.Sink;
import tailor.experiment.plan.Result;
import tailor.structure.Chain;
import tailor.structure.Group;

public class GroupSource implements Operator {
	
	private Chain chain;
	
	private List<Sink<Result>> outputs;
	
	public GroupSource(Chain chain, List<Sink<Result>> outputs) {
		this.chain = chain;
		this.outputs = outputs;
	}

	@Override
	public void run() {
		for (Group group : chain.getGroups()) {
			for (Sink<Result> output : outputs) {
				output.put(new Result(chain, group));
			}
		}
		
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub
		
	}

}
