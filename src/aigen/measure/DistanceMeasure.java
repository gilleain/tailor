package aigen.measure;

import aigen.engine.Engine;
import aigen.geometry.Vector;
import aigen.geometry.Geometry;

class DistanceMeasure extends Measure {
	private String descriptionA;
	private String descriptionB;

	public DistanceMeasure(String descriptionA, String descriptionB) {
		this.descriptionA = descriptionA;
		this.descriptionB = descriptionB;
	}

	@Override
	public Double measure(Object structure) {
		try {
			Vector a = Engine.lookup(this.descriptionA, structure).getCenter();
			Vector b = Engine.lookup(this.descriptionB, structure).getCenter();
			return Geometry.distance(a, b);
		} catch (Exception e) {
			System.out.println(
					"DISTANCE MEASURE FAILURE: " + this.descriptionA + " " + this.descriptionB + " " + e.getMessage());
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
			return "Distance";
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
		return String.format("D(%s, %s)", this.descriptionA, this.descriptionB);
	}
}
