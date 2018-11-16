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
    
    /* 
     * Matches each subdescription of the description to each substructure
     * in the structure. 
     */
    public List<Match> match(Description description, Structure structure) {
        
        List<Match> matches = new ArrayList<>();
        for (Description subDescription : description.getSubDescriptions()) {
            for (Structure subStructure : structure) {
                List<Match> subMatches = 
                    subEngine.match(subDescription, subStructure);
                for (Match subMatch : subMatches) {
                    Structure topLevel = new Structure(structure.getLevel());
                    topLevel.copyProperty(structure, "Name");
                    Match match = new Match(description, topLevel);
                    match.completeMatch(subMatch);
                    match.addSubMatch(subMatch);
                    matches.add(match);
                }
            }
        }
        return matches;
    }

}
