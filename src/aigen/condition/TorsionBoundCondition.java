package aigen.condition;

public class TorsionBoundCondition extends BaseCondition {
	private Object descriptionA;
	private Object descriptionB;
	private Object descriptionC;
	private Object descriptionD;
	private double center;
	private double range;

	public TorsionBoundCondition(Object descriptionA, Object descriptionB, Object descriptionC, Object descriptionD,
			double center, double range, String name) {
		this.descriptionA = descriptionA;
		this.descriptionB = descriptionB;
		this.descriptionC = descriptionC;
		this.descriptionD = descriptionD;
		this.center = center;
		this.range = range;
		this.name = name;
	}

	public TorsionBoundCondition(Object descriptionA, Object descriptionB, Object descriptionC, Object descriptionD,
			double center, double range) {
		this(descriptionA, descriptionB, descriptionC, descriptionD, center, range, "Torsion");
	}

	@Override
	public boolean satisfiedBy(Object example) {
		try {
			// Object a = Engine.lookup(descriptionA, example).center;
			// Object b = Engine.lookup(descriptionB, example).center;
			// Object c = Engine.lookup(descriptionC, example).center;
			// Object d = Engine.lookup(descriptionD, example).center;
			// double t = Geometry.torsion(a, b, c, d);

			// boolean satisfied = center - range < t && t < center + range;
			// if (negated) satisfied = !satisfied;

			// if (satisfied) {
			// setPropertyOnExample(example, name, t);
			// return true;
			// }
			return false;
		} catch (Exception e) {
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
