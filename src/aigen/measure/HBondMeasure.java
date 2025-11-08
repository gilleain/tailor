package aigen.measure;

import aigen.description.DescriptionException;
import aigen.feature.Atom;
import aigen.engine.Engine;
import aigen.geometry.Geometry;

public class HBondMeasure extends BaseMeasure {
	private String donorDescription;
	private String hydrogenDescription;
	private String acceptorDescription;
	private String attachedDescription;

	class HBondResult {
		public final double dhDistance;
		public final double dhaAngle;
		public final double haaAngle;

		public HBondResult(double dhDistance, double dhaAngle, double haaAngle) {
			this.dhDistance = dhDistance;
			this.dhaAngle = dhaAngle;
			this.haaAngle = haaAngle;
		}
	}

	public HBondMeasure(String donorDescription, String hydrogenDescription, String acceptorDescription,
			String attachedDescription) {
		this.donorDescription = donorDescription;
		this.hydrogenDescription = hydrogenDescription;
		this.acceptorDescription = acceptorDescription;
		this.attachedDescription = attachedDescription;
	}

	@Override
	public HBondResult measure(Object structure) {
		try {
			Atom donorAtom = Engine.lookup(this.donorDescription, structure);
			Atom hydrogenAtom = Engine.lookup(this.hydrogenDescription, structure);
			Atom acceptorAtom = Engine.lookup(this.acceptorDescription, structure);
			Atom attachedAtom = Engine.lookup(this.attachedDescription, structure);

			double dhDistance = Geometry.distance(hydrogenAtom.getCenter(), acceptorAtom.getCenter());
			double dhaAngle = Geometry.angle(donorAtom.getCenter(), hydrogenAtom.getCenter(), acceptorAtom.getCenter());
			double haaAngle = Geometry.angle(hydrogenAtom.getCenter(), acceptorAtom.getCenter(),
					attachedAtom.getCenter());

			return new HBondResult(dhDistance, dhaAngle, haaAngle);
		} catch (DescriptionException e) {
			return new HBondResult(0.0, 0.0, 0.0);
		}
	}

	@Override
	public int getNumberOfColumns() {
		return 3;
	}

	@Override
	public String[] getColumnHeaders() {
		String name = getName();
		if (name == null) {
			name = "HBond";
		}
		return new String[] { String.format("%s:D_H", name), String.format("%s:DHA", name),
				String.format("%s:HAA", name) };
	}

	@Override
	public String[] getFormatStrings() {
		return new String[] { "%.2f", "%.2f", "%.2f" };
	}

	@Override
	public String toString() {
		return String.format("HB(%s, %s, %s, %s)", this.donorDescription, this.hydrogenDescription,
				this.acceptorDescription, this.attachedDescription);
	}
}
