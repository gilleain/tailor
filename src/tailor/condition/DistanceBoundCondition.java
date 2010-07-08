package tailor.condition;

import tailor.description.Description;
import tailor.engine.Match;
import tailor.geometry.CenterFinder;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;


/**
 * Constrains two structures to be less than a particular distance.
 * 
 * @author maclean
 *
 */
public class DistanceBoundCondition implements Condition {
	
	private String name;
	private Description descriptionA;
	private Description descriptionB;
	private double center;
	private double range;
	
	public DistanceBoundCondition(String name, Description descriptionA, Description descriptionB, double center, double range) {
		this.name = name;
		this.descriptionA = descriptionA;
		this.descriptionB = descriptionB;
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
					this.descriptionA == o.descriptionA &&
					this.descriptionB == o.descriptionB &&
					this.center == o.center &&
					this.range == o.range) {
				return true;
			}
		}
		return false;
	}

	public boolean satisfiedBy(Match match) {
	    Vector a = CenterFinder.findCenter(descriptionA, match);
	    Vector b = CenterFinder.findCenter(descriptionB, match);

	    double d = Geometry.distance(a, b);
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
