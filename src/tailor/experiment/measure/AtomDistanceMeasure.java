package tailor.experiment.measure;

import java.util.List;

import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.description.DescriptionPath;
import tailor.geometry.Geometry;
import tailor.structure.Atom;

public class AtomDistanceMeasure extends AbstractAtomListMeasure {
	
	public AtomDistanceMeasure(AtomMatcher atomMatcher) {
		super(atomMatcher);
	}
	
	public AtomDistanceMeasure(DescriptionPath... descriptionPaths) {
		super(descriptionPaths);
	}
	
	@Override
	public DoubleMeasurement measure(List<Atom> atoms) {
		return measure(atoms.get(0), atoms.get(1));
	}

	public DoubleMeasurement measure(Atom a, Atom b) {
		return new DoubleMeasurement(Geometry.distance(a.getCenter(), b.getCenter()));
	}

}