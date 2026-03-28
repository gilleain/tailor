package tailor.experiment.condition;

import java.util.List;

import tailor.experiment.measure.AtomDistanceMeasure;
import tailor.structure.Atom;

public class AtomDistanceCondition {
	
	private AtomDistanceMeasure adm;
	private double distance;
	
	public AtomDistanceCondition(double distance) {
		this.distance = distance;
		this.adm = new AtomDistanceMeasure();
	}
	
	public boolean accept(Atom a, Atom b) {
		return adm.measure(a, b) < distance;
	}
	
	public boolean accept(List<Atom> atoms) {
		assert atoms.size() == 2;	// TODO - alternatively return some kind of error condition?
		return accept(atoms.get(0), atoms.get(1));
	}

}
