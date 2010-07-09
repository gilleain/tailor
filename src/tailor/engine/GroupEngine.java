package tailor.engine;

import tailor.Level;
import tailor.condition.Condition;
import tailor.datasource.Structure;
import tailor.description.AtomDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;

/**
 * Matches a group Description to a group.
 * 
 * @author maclean
 *
 */
public class GroupEngine {  // TODO : implement the Engine interface

    /**
     * Perform a single match between a GroupDescription and a group, moving
     * down to match sub-descriptions to sub-structure.
     * 
     * Returns a copy of the group, with references to the substructures. A
     * partial match will result in a partial copy, which should be checked with
     * the fullMatch method.
     * 
     * @param groupDescription
     * @param group
     * @return
     */
    public Match match(GroupDescription groupDescription, Structure group) {
        // a copy is made, so that only the matching atoms are stored
        Structure matchingGroup = new Structure(Level.RESIDUE);
        matchingGroup.copyProperty(group, "Name");
        matchingGroup.copyProperty(group, "Number");
        
        // this match will be filled with atom matches
        Match match = new Match(groupDescription, matchingGroup);
        for (AtomDescription atomDescription : groupDescription) {
            String atomName = atomDescription.getName();
            Structure atom = group.getSubStructureByProperty("Name", atomName);
            
            // null return value means no match found
            if (atom == null) {
                return match;
            } else {
                
                // we don't clone the atom, as it is immutable
                matchingGroup.addSubStructure(atom);
                match.associate(atomDescription, atom);
            }
        }
        return match;
    }
    
    /**
     * Checks that a match made by the match(GroupDescription, Structure) method
     * is complete.
     * 
     * @param groupDescription
     * @param match
     * @return
     */
    public boolean fullMatch(GroupDescription groupDescription, Match match) {
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
