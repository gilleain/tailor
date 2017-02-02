package tailor.measurement;

import tailor.description.Description;
import tailor.geometry.Vector;
import tailor.match.Match;

public class GeometricMeasure {
    
    private final String name;
    
    public GeometricMeasure(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public Vector getPoint(Description description, Match match) {
        return null;    // TODO
    }

}
