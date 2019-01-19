package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.description.Description;
import tailor.match.Match;
import tailor.structure.Level;
import tailor.structure.Protein;
import tailor.structure.Structure;

public class TopLevelEngine extends AbstractBaseEngine {
    
    private Engine subEngine;
    
    public TopLevelEngine(Engine subEngine) {
        this.subEngine = subEngine;
    }
    
    /* 
     * Matches each subdescription of the description to each substructure in the structure. 
     */
    public List<Match> match(Description description, Structure structure) {
        
        List<Match> matches = new ArrayList<>();
        for (Description subDescription : description.getSubDescriptions()) {
            for (Structure subStructure : structure.getSubstructures()) {
                List<Match> subMatches = 
                    subEngine.match(subDescription, subStructure);
                for (Match subMatch : subMatches) {
                    // TODO - could this be changed to a factory?
                    // XXX has now been fixed to one particular level!!
                    Structure topLevel = new Protein("");
                    topLevel.copyProperty(structure, "Name");
                    Match match = new Match(description, topLevel, Level.PROTEIN);
                    match.completeMatch(subMatch);
                    match.addMatch(subMatch);
                    matches.add(match);
                }
            }
        }
        return matches;
    }

}
