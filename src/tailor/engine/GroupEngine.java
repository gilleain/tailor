package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.Level;
import tailor.condition.Condition;
import tailor.condition.PropertyCondition;
import tailor.datasource.Structure;
import tailor.description.Description;

/**
 * Matches a description of a set of atoms (that have no children) to a
 * structure containing a set of atoms.
 * 
 * @author maclean
 *
 */
public class GroupEngine extends AbstractBaseEngine {  

    /**
     * Perform a single match between a GroupDescription and a group, moving
     * down to match sub-descriptions to sub-structure.
     * 
     * Returns a copy of the group, with references to the substructures. A
     * partial match will result in a partial copy, which should be checked with
     * the fullMatch method.
     * 
     * @param description
     * @param structure
     * @return
     */
    public List<Match> match(Description description, Structure structure) {
        List<Match> matches = new ArrayList<Match>();
        Level level = structure.getLevel();
        // a copy is made, so that only the matching atoms are stored
        Structure matchingCopy = new Structure(level);
        matchingCopy.copyProperty(structure, "Name");
        matchingCopy.copyProperty(structure, "Number"); // XXX groups only!
        
        // this match will be filled with atom matches
        Match match = new Match(description, matchingCopy);
        for (Description atomDescription : description.getSubDescriptions()) {
            Structure atom = getMatchingSubstructure(atomDescription, structure);
            // null return value means no match found
            if (nameMatches(atomDescription, atom)) {
                // we don't clone the atom, as it is immutable
                matchingCopy.addSubStructure(atom);
                match.associate(atomDescription, atom);
            } else {
                matches.add(match);
                return matches;
                
            }
        }
        // TODO : this is stupid - make it go away
        matches.add(match);
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
    
    /**
     * Checks that a match made by the match(GroupDescription, Structure) method
     * is complete.
     * 
     * @param groupDescription
     * @param match
     * @return
     */
    public boolean fullMatch(Description groupDescription, Match match) {
        return match.size() == groupDescription.size() 
            && conditionsSatisfied(groupDescription, match);
    }
    
    public boolean conditionsSatisfied(Description description, Match match) {
        for (Condition condition : description.getConditions()) {
            if (condition.satisfiedBy(match)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

}
