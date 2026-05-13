package tailor.condition;

import java.util.logging.Logger;

import tailor.api.Condition;

public class RangeCondition implements Condition<Double> {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private final double minValue;
	
	private final double maxValue;
	
	public RangeCondition(double minValue, double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public boolean accept(Double value) {
		boolean isInRange = minValue < value && value < maxValue;
		logger.fine("Value " + ((isInRange)? "IS" : "NOT") + " between " 
				+ minValue + op(minValue, value) + value + op(value, maxValue) + maxValue);
		return isInRange;
	}
	
	public double getMinValue() {
		return minValue;
	}
	
	public double getMaxValue() {
		return maxValue;
	}
	
	public String op(double a, double b) {
		return a < b? " < " : " > ";
	}

}