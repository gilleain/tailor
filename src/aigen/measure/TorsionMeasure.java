package aigen.measure;

import aigen.engine.Engine;
import aigen.geometry.Vector;
import aigen.geometry.Geometry;

class TorsionMeasure extends BaseMeasure {
	private String descriptionA;
	private String descriptionB;
	private String descriptionC;
	private String descriptionD;

	public TorsionMeasure(String descriptionA, String descriptionB, String descriptionC, String descriptionD) {
		this.descriptionA = descriptionA;
		this.descriptionB = descriptionB;
		this.descriptionC = descriptionC;
		this.descriptionD = descriptionD;
	}

	@Override
	public Double measure(Object structure) {
		try {
			Vector a = Engine.lookup(this.descriptionA, structure).getCenter();
			Vector b = Engine.lookup(this.descriptionB, structure).getCenter();
			Vector c = Engine.lookup(this.descriptionC, structure).getCenter();
			Vector d = Engine.lookup(this.descriptionD, structure).getCenter();
			return Geometry.torsion(a, b, c, d);
		} catch (Exception e) {
			System.out.println("TORSION MEASURE FAILURE: " + this.descriptionA + " " + this.descriptionB + " "
					+ this.descriptionC + " " + this.descriptionD);
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
			return "Torsion";
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
		return String.format("T(%s, %s, %s, %s)", this.descriptionA, this.descriptionB, this.descriptionC,
				this.descriptionD);
	}
}
