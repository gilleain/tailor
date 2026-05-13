package tailor.measurement;

import java.util.Map;
import java.util.stream.Collectors;

import tailor.api.CompositeMeasurement;
import tailor.api.Condition;
import tailor.api.Measurement;

public class CompositeDoubleMeasurement implements CompositeMeasurement<Double> {
	
	private final Map<String, Measurement<Double>> valuesByLabel;
	
	public CompositeDoubleMeasurement(Map<String, Measurement<Double>> valuesByLabel) {
		this.valuesByLabel = valuesByLabel;
	}
	
	public String toString() {
		return valuesByLabel.values().stream().map(Measurement::toString).collect(Collectors.joining("\t"));
	}

	@Override
	public boolean apply(Condition<Double> condition) {
		//	TODO - throw exception :/
		return false;
	}

	@Override
	public boolean apply(String partLabel, Condition<Double> condition) {
		return this.valuesByLabel.get(partLabel).apply(condition);
	}
}
