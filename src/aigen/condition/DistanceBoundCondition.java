package aigen.condition;

class DistanceBoundCondition extends BaseCondition {
	private Object descriptionA;
	private Object descriptionB;
	private double center;
	private double range;

	public DistanceBoundCondition(Object descriptionA, Object descriptionB, double center, double range, String name) {
		this.descriptionA = descriptionA;
		this.descriptionB = descriptionB;
		this.center = center;
		this.range = range;
		this.name = name;
	}

	public DistanceBoundCondition(Object descriptionA, Object descriptionB, double center, double range) {
		this(descriptionA, descriptionB, center, range, "Distance");
	}

	@Override
	public boolean satisfiedBy(Object example) {
		try {
			// Object a = Engine.lookup(descriptionA, example).center;
			// Object b = Engine.lookup(descriptionB, example).center;
			// double dd = Geometry.distance(a, b);

			// For now, return placeholder
			// boolean satisfied = center - range < dd && dd < center + range;
			// if (negated) satisfied = !satisfied;

			// if (satisfied) {
			// setPropertyOnExample(example, name, dd);
			// return true;
			// }
			return false;
		} catch (Exception e) {
			System.err.println("description exception");
			return false;
		}
	}

	private void setPropertyOnExample(Object example, String propName, double value) {
		try {
			java.lang.reflect.Field field = example.getClass().getDeclaredField(propName);
			field.setAccessible(true);
			field.set(example, value);
		} catch (Exception e) {
			// Ignore if field doesn't exist
		}
	}

	@Override
	public String toString() {
		return String.format("%s (%.2f : %.2f)", name, center - range, center + range);
	}
}
