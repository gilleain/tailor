package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.datasource.Structure;
import tailor.description.Description;

public class TopLevelEngine extends AbstractBaseEngine {
    
    private Engine subEngine;
    
    public TopLevelEngine(Engine subEngine) {
        this.subEngine = subEngine;
    }
    
    /* (non-Javadoc)
     * @see tailor.engine.AbstractBaseEngine#match(tailor.description.Description, tailor.datasource.Structure)
     */
    public List<Match> match(Description description, Structure structure) {
        
        List<Match> matches = new ArrayList<Match>();
        for (Description subDescription : description.getSubDescriptions()) {
            for (Structure subStructure : structure) {
                List<Match> subMatches = 
                    subEngine.match(subDescription, subStructure);
                for (Match subMatch : subMatches) {
                    Structure topLevel = new Structure(structure.getLevel());
                    Match match = new Match(description, topLevel);
                    match.completeMatch(subMatch);
                    matches.add(match);
                }
            }
        }
        return matches;
    }

}
