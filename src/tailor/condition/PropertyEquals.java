package tailor.condition;

import tailor.measurement.PropertyMeasurement;

public class PropertyEquals {
	
	private String value;
	
	public boolean accept(PropertyMeasurement propertyMeasurement) {
		return propertyMeasurement.getValue().equals(value);
	}

}
