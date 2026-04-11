package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.List;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.operator.ResultPipe;
import tailor.experiment.operator.ScanAtomResultByLabel;

public class Plan {
	
	private int operatorId;

	private List<Operator> operators;
	
	/**
	 * The 'entry' points to the pipeline, used to
	 * connect the structure source to
	 */
	private List<Operator> startPoints;
	
	public Plan() {
		this.operators = new ArrayList<>();
		this.startPoints = new ArrayList<>();
	}
	
	public void describe() {
		for (Operator operator : this.operators) {
			System.out.println(operator.description());
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
			if (start instanceof ScanAtomResultByLabel scanOperator) {
				ResultPipe input = new ResultPipe();
				scanOperator.setSource(input);
				inputs.add(input);
			}
		}
		return inputs;
	}
	
	public void addStart(Operator operator) {
		this.startPoints.add(operator);
		this.addOperator(operator);
	}
	
	public void addOperator(Operator operator) {
		this.operators.add(operator);
		operator.setId(getOperatorId());
	}
	
	private String getOperatorId() {
		String operatorIdString = String.valueOf(operatorId);
		operatorId++;
		return operatorIdString;
	}
}
