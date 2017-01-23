package tailor.measure;

import tailor.description.DescriptionException;
import tailor.description.Description;
import tailor.engine.Match;
import tailor.geometry.CenterFinder;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;

/**
 * Measures the distance between two points.
 * 
 * @author maclean
 *
 */
public class DistanceMeasure extends Measure {
	 
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
	    Vector a = CenterFinder.findCenter(descriptionA, match);
        Vector b = CenterFinder.findCenter(descriptionB, match);
        return Geometry.distance(a, b);
	}

	@Override
	public Measurement measure(Match match) throws DescriptionException {
		return new Measurement(this.getName(), calculate(match));
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
