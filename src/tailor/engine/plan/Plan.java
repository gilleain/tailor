package tailor.engine.plan;

import java.util.ArrayList;
import java.util.List;

import tailor.api.Operator;
import tailor.engine.operator.Pipe;

/**
 * A plan is a directed graph of operators that carry out a search.
 */
public class Plan {
	
	private int operatorId;

	private List<Operator> operators;
	
	/**
	 * The 'entry' points to the pipeline, used to
	 * connect the structure source to
	 */
	private List<Operator> startPoints;
	
	private Pipe outputPoint;
	
	public Plan() {
		this.operators = new ArrayList<>();
		this.startPoints = new ArrayList<>();
	}
	
	public void describe() {
		for (Operator operator : this.operators) {
			System.out.println(operator.description());
		}
	}
	
	public void clear() {
		for (Operator operator : this.operators) {
			operator.clear();
		}
	}
	
	public List<Operator> getOperators() {
		return this.operators;
	}
	
	public List<Operator> getStartPoints() {
		return this.startPoints;
	}
	
	public List<Pipe> getInputPipes() {
		List<Pipe> inputs = new ArrayList<Pipe>();
		for (Operator start : this.startPoints) {
			Pipe input = new Pipe();
			start.setInput(input);
			inputs.add(input);
		}
		return inputs;
	}
	
	public Pipe addStart(Operator operator) {
		this.startPoints.add(operator);
		return this.addOperatorReturnPipe(operator);
	}
	
	public void addOperator(Operator operator) {
		this.operators.add(operator);
		operator.setId(getOperatorId());
	}
	
	public Pipe addOperatorReturnPipe(Operator operator) {
		return this.addOperatorReturnPipe(operator, null);
	}
		
    public Pipe addOperatorReturnPipe(Operator operator, Pipe inputPipe) {
		addOperator(operator);
		
		Pipe outputPipe = new Pipe();
		operator.setOutput(outputPipe);
		if (inputPipe != null) {
			operator.setInput(inputPipe);
		}
		return outputPipe;
	}
	
	private String getOperatorId() {
		String operatorIdString = String.valueOf(operatorId);
		operatorId++;
		return operatorIdString;
	}

	public Pipe getOutputPoint() {
		return outputPoint;
	}

	public void setOutputPoint(Pipe output) {
		this.outputPoint = output;
	}
}
