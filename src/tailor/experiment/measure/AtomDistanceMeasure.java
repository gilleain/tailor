package tailor.experiment.measure;

import java.util.List;

import tailor.experiment.condition.AtomMatcher;
import tailor.geometry.Geometry;
import tailor.structure.Atom;

public class AtomDistanceMeasure extends AbstractAtomListMeasure {
	
	public AtomDistanceMeasure(AtomMatcher atomMatcher) {
		super(atomMatcher);
	}
	
	@Override
	public double measure(List<Atom> atoms) {
		return measure(atoms.get(0), atoms.get(1));
	}

	public double measure(Atom a, Atom b) {
		return Geometry.distance(a.getCenter(), b.getCenter());
	}

}