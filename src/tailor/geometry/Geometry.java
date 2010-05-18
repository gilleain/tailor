package tailor.geometry;


public class Geometry {

	public static double distance(Vector a, Vector b) {
		return a.minus(b).length();
	}
	
	public static double angle(Vector a, Vector b, Vector c) {
		return Math.toDegrees((b.minus(a)).angle(b.minus(c)));
	}
	
	public static double torsion(Vector a, Vector b, Vector c, Vector d) {
		Vector l = (b.minus(a)).cross(c.minus(b));
		Vector r = (d.minus(c)).cross(b.minus(c));
		double ang = Math.toDegrees(l.angle(r));
		
		if (l.cross(r).multiply(c.minus(b)) < 0.0) {
			return -ang;
		} else {
			return ang;
		}
	}
	
}
