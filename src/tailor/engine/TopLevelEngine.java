package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.datasource.Structure;
import tailor.description.Description;

public class TopLevelEngine {
    
    private Engine subEngine;
    
    public TopLevelEngine(Engine subEngine) {
        this.subEngine = subEngine;
    }
    
    public List<Match> match(Description description, Structure structure) {
        List<Match> matches = new ArrayList<Match>();
        for (Description subDescription : description.getSubDescriptions()) {
        }
        return matches;
    }

}
