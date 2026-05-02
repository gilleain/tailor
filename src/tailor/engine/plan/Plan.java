package tailor.engine.plan;

import java.util.ArrayList;
import java.util.List;

import tailor.api.Operator;
import tailor.api.PipeableOperator;
import tailor.api.Sink;
import tailor.engine.operator.ResultPipe;

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
	
	private ResultPipe outputPoint;
	
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
	
	public List<Sink<Result>> getInputPipes() {
		List<Sink<Result>> inputs = new ArrayList<Sink<Result>>();
		for (Operator start : this.startPoints) {
			if (start instanceof PipeableOperator) {
				PipeableOperator<Result, Result> pipeableOperator = (PipeableOperator<Result, Result>) start;
				ResultPipe input = new ResultPipe();
				pipeableOperator.setSource(input);
				inputs.add(input);
			}
		}
		return inputs;
	}
	
	public ResultPipe addStart(PipeableOperator<Result, Result> operator) {
		this.startPoints.add(operator);
		return this.addOperatorReturnPipe(operator);
	}
	
	public void addOperator(Operator operator) {
		this.operators.add(operator);
		operator.setId(getOperatorId());
	}
	
	public ResultPipe addOperatorReturnPipe(PipeableOperator<Result, Result> operator) {
		this.operators.add(operator);
		ResultPipe output = new ResultPipe();
		operator.setId(getOperatorId());
		operator.setSink(output);
		return output;
	}
	
	private String getOperatorId() {
		String operatorIdString = String.valueOf(operatorId);
		operatorId++;
		return operatorIdString;
	}

	public ResultPipe getOutputPoint() {
//		return (ResultPipe) ((PipeableOperator<?, ?>) operators.get(operators.size() - 1)).getSink(); // TODO ugh
		return outputPoint;
	}

	public void setOutputPoint(ResultPipe output) {
		this.outputPoint = output;
	}
}
