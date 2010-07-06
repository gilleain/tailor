package tailor.measure;

import tailor.description.DescriptionException;
import tailor.engine.Match;

public class HBondMeasure extends Measure {
	
	 public HBondMeasure() {
	    	super("HBond");
	    }

	@Override
	public Measurement measure(Match match) throws DescriptionException {
		// TODO not sure how to 'measure' the hbond.
		// Do we return an energy?
		return null;
	}
	
	public String toString() {
		return "HBond Measure";
	}

}
