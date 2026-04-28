package tailor.condition;

import java.util.logging.Logger;

import tailor.api.Measurement;
import tailor.api.MeasurementCondition;
import tailor.measurement.DoubleMeasurement;

public class UpperBoundCondition implements MeasurementCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private double maxValue;
	
	public UpperBoundCondition(double value) {
		this.maxValue = value;
	}
	
	public boolean accept(Measurement measurement) {
		double actualValue = ((DoubleMeasurement) measurement).getValue();
		logger.fine("Value " + actualValue + ((actualValue < maxValue)? " < " : " > ") + maxValue);
		return actualValue < maxValue;
	}
	
}
