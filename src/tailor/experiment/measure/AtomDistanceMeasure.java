package tailor.experiment.measure;

import tailor.geometry.Geometry;
import tailor.structure.Atom;

public class AtomDistanceMeasure {
	
	public double measure(Atom a, Atom b) {
		return Geometry.distance(a.getCenter(), b.getCenter());
	}

}
