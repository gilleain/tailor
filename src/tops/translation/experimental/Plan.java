package tops.translation.experimental;

import java.util.ArrayList;
import java.util.List;

import tailor.api.Operator;
import tailor.engine.operator.Pipe;

public class Plan {
	
	private int operatorId;
	
	private List<Operator> operators;
	
	private Pipe outputPipe;
	
	private List<Pipe> inputs;
	
	public Plan() {
		this.operators = new ArrayList<>();
		this.inputs = new ArrayList<>();
	}
	
	public Pipe addInputOperator(Operator operator, Pipe input) {
		Pipe output = addOperator(operator);
		this.inputs.add(input);
		return output;
	}
	
	public Pipe addOperator(Operator operator) {
		this.operators.add(operator);
		operator.setId(String.valueOf(operatorId));
		operatorId++;
		
		Pipe output = new Pipe();
		operator.setOutput(output);
		
		return output;
	}

	public Pipe getOutputPipe() {
		return outputPipe;
	}

	public void setOutputPipe(Pipe outputPipe) {
		this.outputPipe = outputPipe;
	}

	public List<Operator> getOperators() {
		return this.operators;
	}

	public List<Pipe> getInputs() {
		return inputs;
	}

}
