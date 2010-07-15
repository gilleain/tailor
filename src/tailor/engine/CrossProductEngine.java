package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.datasource.Structure;
import tailor.description.Description;

public class CrossProductEngine extends AbstractBaseEngine {

    @Override
    public List<Match> match(Description description, Structure structure) {
        List<Match> matches = new ArrayList<Match>();
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
