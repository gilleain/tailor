package tailor.experiment.measure;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tailor.api.Measurement;

public class CompositeMeasurement implements Measurement {
	
	private final List<Measurement> values;
	
	public CompositeMeasurement(Measurement... measurements) {
		this.values = Arrays.asList(measurements);
	}
	
	public String toString() {
		return values.stream().map(Measurement::toString).collect(Collectors.joining("\t"));
	}
}
