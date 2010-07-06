package tailor.measure;

import tailor.description.Description;
import tailor.description.DescriptionException;
import tailor.engine.Match;
import tailor.geometry.CenterFinder;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;


/**
 * Measure an angle between three points.
 * 
 * @author maclean
 *
 */
public class AngleMeasure extends Measure {
    
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


    @Override
    public Measurement measure(Match match) throws DescriptionException {
        Vector a = CenterFinder.findCenter(descriptionA, match);
        Vector b = CenterFinder.findCenter(descriptionA, match);
        Vector c = CenterFinder.findCenter(descriptionA, match);
        
        double angle = Geometry.angle(a, b, c);
        return new Measurement(this.getName(), angle);
    }
    
    public String toString() {
        return "a (" + descriptionA.toPathString() + ", " 
                    + descriptionB.toPathString() + ", "
                    + descriptionC.toPathString() + ") ";
    }

}
