package tailor.condition;

import tailor.api.Condition;

public class PropertyEquals implements Condition<String> {
	
	private final String value;
	
	public PropertyEquals(String value) {
		this.value = value;
	}

	@Override
	public boolean accept(String value) {
		return value.equals(value);
	}
	
	public String toString() {
		return " = " + value;
	}

}
