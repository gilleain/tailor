package tailor.engine.operator;

import java.util.List;

import tailor.api.Operator;
import tailor.engine.plan.Result;
import tailor.structure.Chain;
import tailor.structure.Group;

public class GroupSource implements Operator {
	
	private Chain chain;
	
	private List<Pipe> outputs;
	
	public GroupSource(Chain chain, List<Pipe> outputs) {
		this.chain = chain;
		this.outputs = outputs;
	}

	@Override
	public void run() {
		for (Group group : chain.getGroups()) {
			for (Pipe output : outputs) {
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

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOutput(Pipe output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInput(Pipe input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Pipe getOutput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pipe getInput() {
		// TODO Auto-generated method stub
		return null;
	}

}
