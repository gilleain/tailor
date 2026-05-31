package tailor.measurement;

import javax.vecmath.Point3d;

public class PointMeasurement {
	
	private final Point3d value;

	public PointMeasurement(Point3d value) {
		this.value = value;
	}

	public Point3d getValue() {
		return this.value;
	}

}
