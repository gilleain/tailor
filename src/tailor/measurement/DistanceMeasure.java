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
public class DistanceMeasure extends GeometricMeasure implements Measure<DoubleMeasurement> {
	
	private static final String[] FORMAT_STRINGS = new String[] { "%.2f" };
	 
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
	
	@Override
	public int getNumberOfColumns() {
		return 1;
	}

	@Override
	public String[] getColumnHeaders() {
		String name = getName();
		if (name == null) {
			return new String[] { "Distance" };
		} else {
			return new String[] { name };
		}
	}

	@Override
	public String[] getFormatStrings() {
		return FORMAT_STRINGS;
	}
   
    public Description getDescriptionA() {
        return this.descriptionA;
    }
    
    public Description getDescriptionB() {
        return this.descriptionB;
    }
    
    public String toString() {
        return "D (" + descriptionA.toXmlPathString() 
                  + ", " + descriptionB.toXmlPathString() + ") "; // TODO
    }

}
