package tailor.condition;

import java.util.logging.Logger;

import tailor.api.Condition;

public class IntegerLowerBoundCondition implements Condition<Integer> {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private Integer minValue;
	
	public IntegerLowerBoundCondition(Integer value) {
		this.minValue = value;
	}
	
	public boolean accept(Integer value) {
		logger.fine("Value " + value + ((value > minValue)? " < " : " > ") + minValue);
		return minValue < value;
	}
	
}
