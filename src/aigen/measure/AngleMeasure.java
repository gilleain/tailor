package aigen.measure;

import tailor.engine.Engine;
import tailor.geometry.Geometry;

class AngleMeasure extends Measure {
	private String descriptionA;
	private String descriptionB;
	private String descriptionC;

	public AngleMeasure(String descriptionA, String descriptionB, String descriptionC) {
		this.descriptionA = descriptionA;
		this.descriptionB = descriptionB;
		this.descriptionC = descriptionC;
	}

	@Override
	public Double measure(Object structure) {
		try {
			Point3D a = Engine.lookup(this.descriptionA, structure).getCenter();
			Point3D b = Engine.lookup(this.descriptionB, structure).getCenter();
			Point3D c = Engine.lookup(this.descriptionC, structure).getCenter();
			return Geometry.angle(a, b, c);
		} catch (Exception e) {
			System.out.println(
					"ANGLE MEASURE FAILURE: " + this.descriptionA + " " + this.descriptionB + " " + this.descriptionC);
			return 0.0;
		}
	}

	@Override
	public int getNumberOfColumns() {
		return 1;
	}

	@Override
	public String getColumnHeaders() {
		String name = getName();
		if (name == null) {
			return "Angle";
		} else {
			return name;
		}
	}

	@Override
	public String getFormatStrings() {
		return "%.2f";
	}

	@Override
	public String toString() {
		return String.format("A(%s, %s, %s)", this.descriptionA, this.descriptionB, this.descriptionC);
	}
}
