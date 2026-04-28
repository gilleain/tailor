package tailor.measurement;

import java.util.Map;
import java.util.stream.Collectors;

import tailor.api.Measurement;

public class CompositeMeasurement implements Measurement {
	
	private final Map<String, Measurement> valuesByLabel;
	
	public CompositeMeasurement(Map<String, Measurement> valuesByLabel) {
		this.valuesByLabel = valuesByLabel;
	}
	
	public double getValue(String id) {
		return valuesByLabel.get(id).getValue(id);	// FIXME
	}
	
	public String toString() {
		return valuesByLabel.values().stream().map(Measurement::toString).collect(Collectors.joining("\t"));
	}
}
