package tailor.condition;

import tailor.description.Description;
import tailor.engine.Match;
import tailor.measure.DistanceMeasure;


/**
 * Constrains two structures to be less than a particular distance.
 * 
 * @author maclean
 *
 */
public class DistanceBoundCondition implements Condition {
	
	private String name;
	private DistanceMeasure distanceMeasure;
	private double center;
	private double range;
	
	public DistanceBoundCondition(String name, int idA, int idB,
	        Description context, double center, double range) {
		this.name = name;
		this.distanceMeasure = new DistanceMeasure(idA, idB, context);
		this.center = center;
		this.range = range;
	}
	
	public boolean contains(Description d) {
		// TODO
		return false;
	 }

    public Object clone() {
    	return null;	// TODO
    }
	
	public boolean equals(Condition other) {
		if (other instanceof DistanceBoundCondition) {
			DistanceBoundCondition o = (DistanceBoundCondition) other;
			if (this.name.equals(o.name) &&
					this.distanceMeasure == o.distanceMeasure &&
					this.center == o.center &&
					this.range == o.range) {
				return true;
			}
		}
		return false;
	}

	public boolean satisfiedBy(Match match) {
	    double d = distanceMeasure.calculate(match);
//        System.err.println("distance " + d);
	    boolean satisfied = (center - range < d) && (center + range > d);
	    if (satisfied) {
	        return true;
	    } else {
	        return false;
	    }
	}
    
    public String toXml() {
    	return "";	//TODO
    }

	public String toString() {
		return String.format("%s (%s : %s)", this.name, this.center - this.range, this.center + this.range);
	}
	
}
