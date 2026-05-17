package tailor.measure;

import java.util.List;

import tailor.description.GroupDescriptionPath;
import tailor.geometry.Geometry;
import tailor.measurement.DoubleMeasurement;
import tailor.structure.Atom;

public class AtomDistanceMeasure extends AbstractAtomListMeasure {
	
	public AtomDistanceMeasure(GroupDescriptionPath... descriptionPaths) {
		super("", descriptionPaths);
	}
	
	public AtomDistanceMeasure(List<GroupDescriptionPath> atomDistancePaths) {
		super("", atomDistancePaths);
	}
	
	public AtomDistanceMeasure(String name, GroupDescriptionPath... descriptionPaths) {
		super(name, descriptionPaths);
	}
	
	public AtomDistanceMeasure(String name, List<GroupDescriptionPath> atomDistancePaths) {
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