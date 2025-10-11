package aigen.condition;

class HBondCondition extends BaseCondition {
	private Object donorDescription;
	private Object hydrogenDescription;
	private Object acceptorDescription;
	private Object attachedDescription;
	private double distanceHACutoff;
	private double angleDHACutoff;
	private double angleHAACutoff;

	public HBondCondition(Object donorDescription, Object hydrogenDescription, Object acceptorDescription,
			Object attachedDescription, double distanceHACutoff, double angleDHACutoff, double angleHAACutoff,
			String name) {
		this.donorDescription = donorDescription;
		this.hydrogenDescription = hydrogenDescription;
		this.acceptorDescription = acceptorDescription;
		this.attachedDescription = attachedDescription;
		this.distanceHACutoff = distanceHACutoff;
		this.angleDHACutoff = angleDHACutoff;
		this.angleHAACutoff = angleHAACutoff;
		this.name = name;
	}

	public HBondCondition(Object donorDescription, Object hydrogenDescription, Object acceptorDescription,
			Object attachedDescription, double distanceHACutoff, double angleDHACutoff, double angleHAACutoff) {
		this(donorDescription, hydrogenDescription, acceptorDescription, attachedDescription, distanceHACutoff,
				angleDHACutoff, angleHAACutoff, "HBond");
	}

	@Override
	public boolean satisfiedBy(Object example) {
		try {
			// Object donorAtomPosition = Engine.lookup(donorDescription, example).center;
			// Object hydrogenAtomPosition = Engine.lookup(hydrogenDescription,
			// example).center;
			// Object acceptorAtomPosition = Engine.lookup(acceptorDescription,
			// example).center;
			// Object attachedAtomPosition = Engine.lookup(attachedDescription,
			// example).center;

			// boolean satisfied = false;
			// double h_a = Geometry.distance(hydrogenAtomPosition, acceptorAtomPosition);
			//
			// if (h_a < distanceHACutoff) {
			// double dha = Geometry.angle(donorAtomPosition, hydrogenAtomPosition,
			// acceptorAtomPosition);
			// if (dha > angleDHACutoff) {
			// double haa = Geometry.angle(hydrogenAtomPosition, acceptorAtomPosition,
			// attachedAtomPosition);
			// if (haa > angleHAACutoff) {
			// satisfied = true;
			// }
			// }
			// }
			//
			// if (negated) {
			// return !satisfied;
			// } else if (satisfied) {
			// setPropertyOnExample(example, name + ":H_A", h_a);
			// setPropertyOnExample(example, name + ":DHA", dha);
			// setPropertyOnExample(example, name + ":HAA", haa);
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

	public String featureDonorAcceptorStr() {
		return String.format("%s %s %s %s", donorDescription, hydrogenDescription, acceptorDescription,
				attachedDescription);
	}

	public String geometricParameterStr() {
		return String.format("%.2f %f %f", distanceHACutoff, angleDHACutoff, angleHAACutoff);
	}

	@Override
	public String toString() {
		return String.format("%s (%s %s)", name, featureDonorAcceptorStr(), geometricParameterStr());
	}
}