package tailor.measurement;

import tailor.description.Description;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;
import tailor.match.Match;


/**
 * Measure an angle between three points.
 * 
 * @author maclean
 *
 */
public class AngleMeasure extends GeometricMeasure implements Measure<DoubleMeasurement> {
	
	private static final String[] FORMAT_STRINGS = new String[] { "%.2f" };
    
    private Description descriptionA;
    
    private Description descriptionB;
    
    private Description descriptionC;

    public AngleMeasure(String name, Description descriptionA, 
            Description descriptionB, Description descriptionC) {
        super(name);
        this.descriptionA = descriptionA;
        this.descriptionB = descriptionB;
        this.descriptionC = descriptionC;
    }
    
    /**
     * @param descriptionA
     * @param descriptionB
     * @param descriptionC
     */
    public AngleMeasure(Description descriptionA, Description descriptionB, 
            Description descriptionC) {
        this("Angle", descriptionA, descriptionB, descriptionC);
    }

    public double calculate(Match match) {
        Vector a = getPoint(descriptionA, match);
        Vector b = getPoint(descriptionB, match);
        Vector c = getPoint(descriptionC, match);
        return Geometry.angle(a, b, c);
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
			return new String[] { "Angle" };
		} else {
			return new String[] { name };
		}
	}

	@Override
	public String[] getFormatStrings() {
		return FORMAT_STRINGS;
	}
    
    public String toString() {
        return "A (" + descriptionA.toPathString() + ", " 
                    + descriptionB.toPathString() + ", "
                    + descriptionC.toPathString() + ") ";
    }

}
