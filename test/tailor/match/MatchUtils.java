package tailor.match;

import java.util.List;

import tailor.description.Description;
import tailor.structure.Structure;

public class MatchUtils {
    
    /**
     * Zip up a match from a description and a structure.
     * 
     * @param description
     * @param structure
     * @return
     */
    public Match zip(Description description, Structure structure) {
        Match match = new Match(description, structure, description.getLevel());
        // TODO - use a visitor? or allow external iteration?
        List<? extends Description> subDescriptions = description.getSubDescriptions();
//        List<Structure> subStructures = structure.getSubstructures();
        for (int index = 0; index < subDescriptions.size(); index++) {
//            match.addMatch(zip(subDescription));
        }
        return match;
    }

}
