package tailor.experiment.measure;

import java.util.List;

import tailor.experiment.condition.AtomMatcher;
import tailor.geometry.Geometry;
import tailor.structure.Atom;

public class AtomAngleMeasure extends AbstractAtomListMeasure {
	
	public AtomAngleMeasure(AtomMatcher atomMatcher) {
		super(atomMatcher);
	}
	
	@Override
	public double measure(List<Atom> atoms) {
		return measure(atoms.get(0), atoms.get(1), atoms.get(2));
	}

	public double measure(Atom a, Atom b, Atom c) {
		return Geometry.angle(a.getCenter(), b.getCenter(), c.getCenter());
	}

}
