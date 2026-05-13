package tailor.measurement;

import tailor.api.Condition;
import tailor.api.Measurement;

public class SimpleMeasurement<T> implements Measurement<T> {

	private final T value;

	public SimpleMeasurement(T value) {
		this.value = value;
	}

	@Override
	public boolean apply(Condition<T> condition) {
		return condition.accept(value);
	}

}
