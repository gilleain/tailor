package tailor.engine;

import tailor.Level;
import tailor.condition.Condition;
import tailor.datasource.Structure;
import tailor.description.AtomDescription;
import tailor.description.GroupDescription;

/**
 * Matches a group Description to a group.
 * 
 * @author maclean
 *
 */
public class GroupEngine {  // TODO : implement the Engine interface

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
    
    public boolean fullMatch(GroupDescription groupDescription, Match match) {
        return match.size() == groupDescription.size() 
            && conditionsSatisfied(groupDescription, match);
    }
    
    public boolean conditionsSatisfied(
            GroupDescription groupDescription, Match match) {
        for (Condition condition : groupDescription.getConditions()) {
            if (condition.satisfiedBy(match)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
    
}
