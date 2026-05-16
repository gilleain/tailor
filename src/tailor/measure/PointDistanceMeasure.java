package tailor.measure;

import javax.vecmath.Point3d;

import tailor.geometry.Geometry;
import tailor.measurement.DoubleMeasurement;

public class PointDistanceMeasure {
	
	
	public DoubleMeasurement measure(Point3d a, Point3d b) {
		return new DoubleMeasurement(Geometry.distance(a, b));
	}

}
