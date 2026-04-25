package tailor.measure;

import java.util.List;

import tailor.description.DescriptionPath;
import tailor.geometry.Geometry;
import tailor.structure.Atom;

public class AtomAngleMeasure extends AbstractAtomListMeasure {
	
	public AtomAngleMeasure(DescriptionPath... descriptionPaths) {
		super("", descriptionPaths);
	}
	
	public AtomAngleMeasure(List<DescriptionPath> descriptionPaths) {
		super("", descriptionPaths);
	}
	
	public AtomAngleMeasure(String name, DescriptionPath... descriptionPaths) {
		super(name, descriptionPaths);
	}
	
	public AtomAngleMeasure(String name, List<DescriptionPath> descriptionPaths) {
		super(name, descriptionPaths);
	}
	
	@Override
	public DoubleMeasurement measure(List<Atom> atoms) {
		return measure(atoms.get(0), atoms.get(1), atoms.get(2));
	}

	public DoubleMeasurement measure(Atom a, Atom b, Atom c) {
		return new DoubleMeasurement(Geometry.angle(a.getCenter(), b.getCenter(), c.getCenter()));
	}

}
