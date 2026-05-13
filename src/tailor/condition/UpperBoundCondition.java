package tailor.condition;

import java.util.logging.Logger;

import tailor.api.Condition;

public class UpperBoundCondition implements Condition<Double> {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private double maxValue;
	
	public UpperBoundCondition(double value) {
		this.maxValue = value;
	}
	
	public boolean accept(Double value) {
		logger.fine("Value " + value + ((value < maxValue)? " < " : " > ") + maxValue);
		return value < maxValue;
	}
	
}
