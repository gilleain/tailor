package tailor.experiment.measure;

import java.util.List;

import tailor.experiment.condition.AtomMatcher;
import tailor.geometry.Geometry;
import tailor.structure.Atom;

public class AtomTorsionMeasure extends AbstractAtomListMeasure {
	
	public AtomTorsionMeasure(AtomMatcher atomMatcher) {
		super(atomMatcher);
	}
	
	@Override
	public double measure(List<Atom> atoms) {
		return measure(atoms.get(0), atoms.get(1), atoms.get(2), atoms.get(3));
	}

	public double measure(Atom a, Atom b, Atom c, Atom d) {
		return Geometry.torsion(a.getCenter(), b.getCenter(), c.getCenter(), d.getCenter());
	}

}
