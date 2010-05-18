package tailor.measure;

import tailor.datasource.Structure;
import tailor.description.DescriptionException;

public class HBondMeasure extends Measure {
	
	 public HBondMeasure() {
	    	super("HBond");
	    }

	@Override
	public Measurement measure(Structure structure) throws DescriptionException {
		// TODO not sure how to 'measure' the hbond.
		// Do we return an energy?
		return null;
	}
	
	public String toString() {
		return "HBond Measure";
	}

}
