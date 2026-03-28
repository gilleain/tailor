package tailor.experiment.measure;

import tailor.geometry.Geometry;
import tailor.structure.Atom;

public class AtomAngleMeasure {
	
	public double measure(Atom a, Atom b, Atom c) {
		return Geometry.angle(a.getCenter(), b.getCenter(), c.getCenter());
	}

}
