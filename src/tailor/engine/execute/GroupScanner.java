package tailor.engine.execute;

import java.util.ArrayList;
import java.util.List;

import tailor.description.Description;
import tailor.match.Match;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Level;

/**
 * Scan a chain for contiguous segments of groups.
 * 
 * @author gilleain
 *
 */
public class GroupScanner {
    
    /**
     * The number of groups to return in each match
     */
    private final int tupleSize;
    
    public GroupScanner(int tupleSize) {
        this.tupleSize = tupleSize;
    }
    
    public List<Match> scan(Chain chain) {
        List<Match> matches = new ArrayList<>();
        
        List<Group> groups = chain.getGroups();
        int max = groups.size() - tupleSize;
        for (int index = 0; index <= max; index++) {
            matches.add(makeMatch(chain, groups, index));
        }
        
        return matches;
    }
    
    private Match makeMatch(Chain chain, List<Group> groups, int startIndex) {
        Description description = null; // TODO!
        
        // make a match container
        Match match = new Match(description, chain, Level.CHAIN);
        int max = startIndex + tupleSize;
        for (int index = startIndex; index < max; index++) {
            Group group = groups.get(index);
            Description groupDescription = null;    // TODO
            match.addMatch(new Match(groupDescription, group, Level.RESIDUE));
        }
        
        return match;
    }

}
