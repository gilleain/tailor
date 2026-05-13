package tailor.measurement;

import tailor.api.Condition;
import tailor.api.Measurement;

public class DoubleMeasurement implements Measurement<Double> {
	
	private final double value;
	
	public DoubleMeasurement() {
		this.value = -1;	// TODO
	}
	
	public DoubleMeasurement(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public String toString() {
		return String.format("%2.2f", value);
	}

	@Override
	public boolean apply(Condition<Double> condition) {
		return condition.accept(this.value);
	}
}
