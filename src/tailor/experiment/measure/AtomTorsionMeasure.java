package tailor.experiment.measure;

import tailor.geometry.Geometry;
import tailor.structure.Atom;

public class AtomTorsionMeasure {
	
	public double measure(Atom a, Atom b, Atom c, Atom d) {
		return Geometry.torsion(a.getCenter(), b.getCenter(), c.getCenter(), d.getCenter());
	}

}
