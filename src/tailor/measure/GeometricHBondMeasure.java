package tailor.measure;

import tailor.description.DescriptionException;
import tailor.engine.Match;

public class GeometricHBondMeasure extends Measure {
    
    private DistanceMeasure haDistanceMeasure;
    
    private AngleMeasure dhaAngleMeasure;
    
    private AngleMeasure haaAngleMeasure;
    
    public GeometricHBondMeasure() {
        super("GeometricHBond");
    }

    public GeometricHBondMeasure(String name) {
        super(name);
    }

    @Override
    public Measurement measure(Match match) throws DescriptionException {
        // TODO Auto-generated method stub
        return null;
    }

}
