package tailor.condition;

import java.util.logging.Logger;

import tailor.api.AtomListCondition;
import tailor.api.AtomListMeasure;
import tailor.measure.DoubleMeasurement;

public class AtomUpperBoundCondition implements AtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private double maxValue;
	
	private AtomListMeasure atomListMeasure;
	
	public AtomUpperBoundCondition(double distance, AtomListMeasure atomListMeasure) {
		this.maxValue = distance;
		this.atomListMeasure = atomListMeasure;
	}
	
	public boolean accept(AtomPartition atoms) {
		double actualValue = ((DoubleMeasurement) atomListMeasure.measure(atoms)).getValue();
		logger.fine("Value " + actualValue + ((actualValue < maxValue)? " < " : " > ") + maxValue);
		return actualValue < maxValue;
	}
	
}
