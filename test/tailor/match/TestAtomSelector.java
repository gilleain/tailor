package tailor.match;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.structure.Atom;
import tailor.structure.Group;
import tailor.structure.Structure;

public class TestAtomSelector {
    
    @Test
    public void twoLayer() {
        Description groupD = new GroupDescription();
        Group groupS = new Group();
        Match match = new Match(groupD, groupS);
        makeAtomSubmatches(match, groupS, "N", "CA", "C", "O");
    }

    private void makeAtomSubmatches(Match match, Group group, String... atomNames) {
        for (String atomName : atomNames) {
            Description atomDescription = new AtomDescription(atomName);
            Atom atom = new Atom(atomName);
            group.addAtom(atom);
            match.addMatch(new Match(atomDescription, atom));
        }
    }

}
