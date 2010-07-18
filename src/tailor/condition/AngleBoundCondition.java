package tailor.condition;

import tailor.description.Description;
import tailor.engine.Match;
import tailor.measure.AngleMeasure;


/**
 * @author maclean
 *
 */
public class AngleBoundCondition implements Condition {

    private String name;
    private AngleMeasure angleMeasure;
    private double center;
    private double range;

    public AngleBoundCondition(String name, Description descriptionA, Description descriptionB, Description descriptionC, double center, double range) {
        this.name = name;
        this.angleMeasure = 
            new AngleMeasure(descriptionA, descriptionB, descriptionC);
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
                    this.angleMeasure == o.angleMeasure &&
                    this.center == o.center &&
                    this.range == o.range) {
                return true;
            }
        }
        return false;
    }
    
    public boolean satisfiedBy(Match match) {
        double angle = angleMeasure.calculate(match);
        boolean satisfied = center - range < angle 
                         && center + range > angle;
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
