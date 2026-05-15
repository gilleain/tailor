package tops.translation.model;

import javax.vecmath.Point3d;

public class Atom {
	
	private final String label;
	
	private final Point3d center;

	public Atom(String label, Point3d center) {
		this.label = label;
		this.center = center;
	}

	public String getLabel() {
		return label;
	}

	public Point3d getCenter() {
		return center;
	}

}
