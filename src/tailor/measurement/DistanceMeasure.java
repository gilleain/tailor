package tailor.measurement;

import tailor.description.Description;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;
import tailor.match.Match;

/**
 * Measures the distance between two points.
 * 
 * @author maclean
 *
 */
public class DistanceMeasure extends GeometricMeasure implements Measure<DoubleMeasurement>{
	 
    private Description descriptionA;
    
    private Description descriptionB;
    
	/**
     * @param name the name of this measure
	 * @param descriptionA
	 * @param descriptionB
	 */
	public DistanceMeasure(String name, Description descriptionA, Description descriptionB) {
	    super(name);
		this.descriptionA = descriptionA;
		this.descriptionB = descriptionB;
	}
	
	public double calculate(Match match) {
	    Vector a = getPoint(descriptionA, match);
        Vector b = getPoint(descriptionB, match);
        return Geometry.distance(a, b);
	}

	@Override
	public DoubleMeasurement measure(Match match) {
		return new DoubleMeasurement(calculate(match));
	}
    
    public String toString() {
        return "d (" + descriptionA.toXmlPathString() 
                  + ", " + descriptionB.toXmlPathString() + ") "; // TODO
    }

    public Description getDescriptionA() {
        return this.descriptionA;
    }
    
    public Description getDescriptionB() {
        return this.descriptionB;
    }

}
