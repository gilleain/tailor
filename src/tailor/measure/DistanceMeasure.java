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

	private int idA;
    
	private int idB;
	
	// possibly needed for printing out details about the endpoints...
	private Description context;   

    /**
     * @param descriptionA
     * @param descriptionB
     */
    public DistanceMeasure(int idA, int idB, Description context) {
        this("Distance", idA, idB, context);
    }
    
	/**
     * @param name the name of this measure
	 * @param descriptionA
	 * @param descriptionB
	 */
	public DistanceMeasure(String name, int idA, int idB, Description context) {
	    super(name);
		this.idA = idA;
		this.idB = idB;
		this.context = context;
	}
	
	public double calculate(Match match) {
	    Vector a = CenterFinder.findCenter(idA, match);
        Vector b = CenterFinder.findCenter(idB, match);
        return Geometry.distance(a, b);
	}

	@Override
	public Measurement measure(Match match) throws DescriptionException {
		return new Measurement(this.getName(), calculate(match));
	}
    
    public String toString() {
        return "d (" + idA + ", " + idB + ") "; // TODO
    }

}
