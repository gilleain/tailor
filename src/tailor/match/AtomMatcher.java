package tailor.match;

import java.util.ArrayList;
import java.util.List;

import tailor.description.AtomDescription;
import tailor.description.GroupDescription;
import tailor.structure.Atom;
import tailor.structure.Group;

/**
 * Compare a group description to a group, to find the matching atoms.
 * 
 * @author maclean
 *
 */
public class AtomMatcher {
    
    public List<Match> match(GroupDescription description, Group group) {
        List<Match> matches = new ArrayList<>();
        for (AtomDescription atomDescription : description) {
            for (Atom atom : group.getAtoms()) {    // TODO - more efficient to do hashmap lookup
                if (atom.getName().equals(atomDescription.getName())) {
                    matches.add(new Match(atomDescription, atom));
                }
            }
        }
        return matches;
    }

}
