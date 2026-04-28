package tailor.condition;

import java.util.logging.Logger;

import tailor.api.Measurement;
import tailor.api.MeasurementCondition;
import tailor.measurement.DoubleMeasurement;

public class LowerBoundCondition implements MeasurementCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private double minValue;
	
	public LowerBoundCondition(double value) {
		this.minValue = value;
	}
	
	public boolean accept(Measurement measurement) {
		double actualValue = ((DoubleMeasurement) measurement).getValue();
		logger.fine("Value " + actualValue + ((actualValue > minValue)? " < " : " > ") + minValue);
		return minValue < actualValue;
	}
	
}
