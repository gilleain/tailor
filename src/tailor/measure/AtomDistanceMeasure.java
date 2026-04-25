package tailor.measure;

import java.util.List;

import tailor.description.DescriptionPath;
import tailor.geometry.Geometry;
import tailor.structure.Atom;

public class AtomDistanceMeasure extends AbstractAtomListMeasure {
	
	public AtomDistanceMeasure(DescriptionPath... descriptionPaths) {
		super("", descriptionPaths);
	}
	
	public AtomDistanceMeasure(List<DescriptionPath> atomDistancePaths) {
		super("", atomDistancePaths);
	}
	
	public AtomDistanceMeasure(String name, DescriptionPath... descriptionPaths) {
		super(name, descriptionPaths);
	}
	
	public AtomDistanceMeasure(String name, List<DescriptionPath> atomDistancePaths) {
		super(name, atomDistancePaths);
	}

	@Override
	public DoubleMeasurement measure(List<Atom> atoms) {
		return measure(atoms.get(0), atoms.get(1));
	}

	public DoubleMeasurement measure(Atom a, Atom b) {
		return new DoubleMeasurement(Geometry.distance(a.getCenter(), b.getCenter()));
	}

}