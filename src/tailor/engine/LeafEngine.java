package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.condition.Condition;
import tailor.condition.PropertyCondition;
import tailor.datasource.Structure;
import tailor.description.Description;

/**
 * Match a group description to a structure and return a list of matches to
 * the leaf elements (atoms). 
 * 
 * @author maclean
 *
 */
public class LeafEngine extends AbstractBaseEngine {

    @Override
    public List<Match> match(Description description, Structure structure) {
        List<Match> matches = new ArrayList<Match>();
        for (Description atomDescription : description.getSubDescriptions()) {
            Structure atom = getMatchingSubstructure(atomDescription, structure);
            if (nameMatches(atomDescription, atom)) {
                // we don't clone the atom, as it is immutable
                matches.add(new Match(atomDescription, atom));
            }
        }
        return matches;
    }
    
    // TODO : this is expensive!
    private Structure getMatchingSubstructure(
            Description description, Structure structure) {
        for (Structure subStructure : structure) {
            if (nameMatches(description, subStructure)) {
                return subStructure;
            }
        }
        return null;
    }
    
    // TODO : this is expensive!
    private boolean nameMatches(Description description, Structure structure) {
        if (structure == null) return false;
        
        String name = null;
        for (Condition condition : description.getConditions()) {
            if (condition instanceof PropertyCondition) {
                PropertyCondition prop = (PropertyCondition) condition;
                if (prop.keyEquals("Name")) {
                    name = prop.getValue();
                    break;
                }
            }
        }
        
        if (name != null) {
            return structure.hasPropertyEqualTo("Name", name);
        }
        return false;
    }

}
