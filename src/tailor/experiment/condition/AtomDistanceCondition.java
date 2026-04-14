package tailor.experiment.condition;

import java.util.List;
import java.util.logging.Logger;

import tailor.experiment.condition.AtomMatcher.Match;
import tailor.experiment.measure.AtomDistanceMeasure;
import tailor.structure.Atom;

public class AtomDistanceCondition extends AbstractAtomListCondition {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomDistanceMeasure adm;
	private double distance;
	
	public AtomDistanceCondition(AtomMatcher atomMatcher, double distance) {
		super(atomMatcher);
		this.distance = distance;
		this.adm = new AtomDistanceMeasure();
	}
	
	public boolean accept(Atom a, Atom b) {
		double actualDistance = adm.measure(a, b);
		logger.fine("Distance " + actualDistance + ((actualDistance < distance)? " < " : " > ") + distance);
		return actualDistance < distance;
	}
	
	public boolean accept(List<Atom> atoms) {
		assert atoms.size() == 2;	// TODO - alternatively return some kind of error condition?
		return accept(atoms.get(0), atoms.get(1));
	}
	
	public boolean accept(Match match) {
		return accept(match.getAtoms().get(0), match.getAtoms().get(1));
	}
}
