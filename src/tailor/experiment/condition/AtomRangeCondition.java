package tailor.experiment.condition;

import java.util.logging.Logger;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.AtomListMeasure;
import tailor.experiment.measure.DoubleMeasurement;

public class AtomRangeCondition implements AtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private final double minValue;
	
	private final double maxValue;
	
	private final AtomListMeasure measure;

	public AtomRangeCondition(double minValue, double maxValue, AtomListMeasure measure) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.measure = measure;
	}

	@Override
	public boolean accept(AtomPartition atomPartition) {
		double actualValue = ((DoubleMeasurement) measure.measure(atomPartition)).getValue();
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