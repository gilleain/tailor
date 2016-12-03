package tailor.match;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.GroupDescription;
import tailor.structure.Atom;
import tailor.structure.Group;

public class TestAtomMatcher {
    
    /**
     * Single atom from a group.
     */
    @Test
    public void singleAtomMatch() {
        final String targetName = "CA";
        GroupDescription groupDescription = makeGroupDescription(targetName);
        Group group = makeGroup("N", "CA", "C", "O");
        
        AtomMatcher matcher = new AtomMatcher();
        List<Match> matches = matcher.match(groupDescription, group);
        assertEquals(1, matches.size());
        assertEquals(targetName, matches.get(0).getStructure().getName());
    }
    
    private GroupDescription makeGroupDescription(String... atomNames) {
        GroupDescription groupDescription = new GroupDescription();
        for (String atomName : atomNames) {
            groupDescription.addAtomDescription(new AtomDescription(atomName));
        }
        return groupDescription;
    }
    
    private Group makeGroup(String... atomNames) {
        Group group = new Group();
        for (String atomName : atomNames) {
            group.addAtom(new Atom(atomName));
        }
        return group;
    }
   

}
