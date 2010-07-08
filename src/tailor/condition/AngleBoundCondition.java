package tailor.condition;

import tailor.description.Description;
import tailor.engine.Match;
import tailor.geometry.CenterFinder;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;


/**
 * @author maclean
 *
 */
public class AngleBoundCondition implements Condition {

    private String name;
    private Description descriptionA;
    private Description descriptionB;
    private Description descriptionC;
    private double center;
    private double range;

    public AngleBoundCondition(String name, Description descriptionA, Description descriptionB, Description descriptionC, double center, double range) {
        this.name = name;
        this.descriptionA = descriptionA;
        this.descriptionB = descriptionB;
        this.descriptionC = descriptionC;
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
        if (other instanceof AngleBoundCondition) {
            AngleBoundCondition o = (AngleBoundCondition) other;
            if (this.name.equals(o.name) &&
                    this.descriptionA == o.descriptionA &&
                    this.descriptionB == o.descriptionB &&
                    this.descriptionC == o.descriptionC &&
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
        Vector c = CenterFinder.findCenter(descriptionC, match);

        double angle = Geometry.angle(a, b, c);
        boolean satisfied = center - range < angle && center + range > angle;
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
        return String.format("%s (%s : %s)", name, center - range, center + range);
    }
    
}
