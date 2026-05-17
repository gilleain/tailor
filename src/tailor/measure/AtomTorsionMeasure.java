package tailor.measure;

import java.util.List;

import tailor.description.GroupDescriptionPath;
import tailor.geometry.Geometry;
import tailor.measurement.DoubleMeasurement;
import tailor.structure.Atom;

public class AtomTorsionMeasure extends AbstractAtomListMeasure {
	
	public AtomTorsionMeasure(GroupDescriptionPath... descriptionPaths) {
		super("", descriptionPaths);
	}
	
	public AtomTorsionMeasure(List<GroupDescriptionPath> descriptionPaths) {
		super("", descriptionPaths);
	}
	
	public AtomTorsionMeasure(String name, GroupDescriptionPath... descriptionPaths) {
		super(name, descriptionPaths);
	}
	
	public AtomTorsionMeasure(String name, List<GroupDescriptionPath> descriptionPaths) {
		super(name, descriptionPaths);
	}
	
	@Override
	public DoubleMeasurement measure(List<Atom> atoms) {
		return measure(atoms.get(0), atoms.get(1), atoms.get(2), atoms.get(3));
	}

	public DoubleMeasurement measure(Atom a, Atom b, Atom c, Atom d) {
		return new DoubleMeasurement(Geometry.torsion(a.getCenter(), b.getCenter(), c.getCenter(), d.getCenter()));
	}

}
