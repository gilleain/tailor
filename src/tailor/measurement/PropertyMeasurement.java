package tailor.measurement;

public class PropertyMeasurement implements Measurement {
	
	private final String value;
	
	public PropertyMeasurement(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}

}
