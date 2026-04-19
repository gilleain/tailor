package tailor.experiment.measure;

import java.util.List;

import tailor.experiment.description.DescriptionPath;
import tailor.geometry.Geometry;
import tailor.structure.Atom;

public class AtomTorsionMeasure extends AbstractAtomListMeasure {
	
	public AtomTorsionMeasure(DescriptionPath... descriptionPaths) {
		super(descriptionPaths);
	}
	
	public AtomTorsionMeasure(List<DescriptionPath> descriptionPaths) {
		super(descriptionPaths);
	}
	
	@Override
	public DoubleMeasurement measure(List<Atom> atoms) {
		return measure(atoms.get(0), atoms.get(1), atoms.get(2), atoms.get(3));
	}

	public DoubleMeasurement measure(Atom a, Atom b, Atom c, Atom d) {
		return new DoubleMeasurement(Geometry.torsion(a.getCenter(), b.getCenter(), c.getCenter(), d.getCenter()));
	}

}
