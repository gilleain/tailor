package aigen.condition;

class PropertyCondition extends BaseCondition {
	private String propertyKey;
	private Object propertyValue;

	public PropertyCondition(String propertyKey, Object propertyValue) {
		this.propertyKey = propertyKey;
		this.propertyValue = propertyValue;
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public Object getPropertyValue() {
		return propertyValue;
	}

	@Override
	public boolean satisfiedBy(Object feature) {
		try {
			// Use reflection to check if feature has the property
			java.lang.reflect.Field field = feature.getClass().getDeclaredField(propertyKey);
			field.setAccessible(true);
			Object value = field.get(feature);
			return value != null && value.equals(propertyValue);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			return false;
		}
	}

	@Override
	public String toString() {
		return propertyKey + " : " + propertyValue;
	}
}
