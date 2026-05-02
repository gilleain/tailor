package tailor.condition;

import tailor.measurement.PropertyMeasurement;

public class PropertyEquals {
	
	private final String value;
	
	public PropertyEquals(String value) {
		this.value = value;
	}
	
	public boolean accept(PropertyMeasurement propertyMeasurement) {
		return propertyMeasurement.getValue().equals(value);
	}
	
	public String toString() {
		return " = " + value;
	}

}
