package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.description.Description;
import tailor.match.Match;
import tailor.structure.Structure;

public class CrossProductEngine extends AbstractBaseEngine {

    @Override
    public List<Match> match(Description description, Structure structure) {
        List<Match> matches = new ArrayList<>();
        for (Description subDescription : description.getSubDescriptions()) {
            extend(matches, subDescription, structure);
        }
        return matches;
    }
    
    private void extend(List<Match> partialMatches, 
            Description description, Structure structure) {
        for (Match partialMatch : partialMatches) {
            
        }
    }

}
