package tailor.match;

import java.util.ArrayList;
import java.util.List;

import tailor.condition.Condition;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;
import tailor.structure.Chain;
import tailor.structure.Group;

/**
 * Match a chain description to a chain. 
 * 
 * @author maclean
 *
 */
public class GroupMatcher {
    
    public List<Match> match(ChainDescription description, Chain chain) {
        List<Match> matches = new ArrayList<>();
        
        // guard for empty descriptions
        if (description.getGroupDescriptions().isEmpty()) return matches; 
        
        List<Match> partialMatches = new ArrayList<>();
        for (Group currentGroup : chain.getGroups()) {
            
            List<Match> partialMatchesToExtend = new ArrayList<>();
            
            // extend all the existing partial matches
            for (Match partial : partialMatches) {
                
                // get the current description 
                GroupDescription currentGroupDescription = 
                        getCurrentGroupDescription(description, partial);
                
                // guard for partials longer than the description
                if (currentGroupDescription == null) continue;
                
                // can we extend this partial?
                if (canExtend(currentGroupDescription, currentGroup)) {
                    addTo(partial, currentGroupDescription, currentGroup);
                    partialMatchesToExtend.add(partial);
                }
                
                // or is it complete, and can be added to the results?
                if (isComplete(description, partial)) {
                    matches.add(partial);
                }
                
                partialMatches = partialMatchesToExtend;
            }
            
            // attempt to find a new match starting at this point
            Match newMatch = startNewMatch(description, chain, currentGroup);
            if (newMatch != null) {
                partialMatches.add(newMatch);
            }
        }
        
        return matches;
    }
    
    private GroupDescription getCurrentGroupDescription(ChainDescription description, Match partial) {
        int partialLength = partial.getLevelSize();
        if (partialLength < description.getGroupDescriptions().size()) {
            return description.getGroupDescriptions().get(partialLength);
        } else {
            return null;
        }
    }
    
    private Match startNewMatch(ChainDescription chainDescription, Chain chain, Group currentGroup) {
        GroupDescription groupDescription = chainDescription.getGroupDescription(0);
        Match match = null;
        if (groupDescription.getName() == null
                || groupDescription.nameMatches(currentGroup.getName())) {
            match = new Match(chainDescription, chain);
            match.addMatch(new Match(groupDescription, currentGroup));
        }
        return match;
    }

    private boolean canExtend(GroupDescription groupDescription, Group group) {
        return groupDescription.getName() == null || groupDescription.nameMatches(group.getName());
    }
    
    private void addTo(Match partial, GroupDescription groupDescription, Group group) {
        partial.addMatch(new Match(groupDescription, group));
    }

    private boolean isComplete(ChainDescription description, Match partial) {
        // the size of the description at the next level
        int expectedSize = description.getGroupDescriptions().size();
        
        for (Condition condition : description.getConditions()) {
//            condition.satisfiedBy(partial); XXX argh!
        }
        
        return partial.getLevelSize() == expectedSize;
    }

}
