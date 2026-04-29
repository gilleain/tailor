package tailor.measure;

import tailor.geometry.Geometry;
import tailor.geometry.Vector;
import tailor.measurement.DoubleMeasurement;

public class PointDistanceMeasure {
	
	
	public DoubleMeasurement measure(Vector a, Vector b) {
		return new DoubleMeasurement(Geometry.distance(a, b));
	}

}
