package tailor.experiment.measure;

import tailor.experiment.api.Measurement;

public class DoubleMeasurement implements Measurement {
	
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
}
