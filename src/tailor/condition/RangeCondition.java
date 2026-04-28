package tailor.condition;

import java.util.logging.Logger;

import tailor.api.Measurement;
import tailor.api.MeasurementCondition;
import tailor.measurement.DoubleMeasurement;

public class RangeCondition implements MeasurementCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private final double minValue;
	
	private final double maxValue;
	
	public RangeCondition(double minValue, double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public boolean accept(Measurement measurement) {
		double actualValue = ((DoubleMeasurement) measurement).getValue();
		boolean isInRange = minValue < actualValue && actualValue < maxValue;
		logger.fine("Value " + ((isInRange)? "IS" : "NOT") + " between " 
				+ minValue + op(minValue, actualValue) + actualValue + op(actualValue, maxValue) + maxValue);
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