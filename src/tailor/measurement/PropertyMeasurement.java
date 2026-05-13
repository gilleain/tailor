package tailor.measurement;

import tailor.api.Condition;
import tailor.api.Measurement;

public class PropertyMeasurement implements Measurement<String> {
	
	private final String value;

	public PropertyMeasurement(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean apply(Condition<String> condition) {
		return condition.accept(value);
	}

}
