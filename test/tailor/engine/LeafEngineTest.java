package tailor.engine;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.match.Match;
import tailor.structure.Atom;
import tailor.structure.Group;

public class LeafEngineTest {
    
    public void addAtom(Group group, String name) {
        group.addAtom(new Atom(name));
    }
    
    @Test
    public void simpleTest() {
        Description groupDescription = new GroupDescription();
        groupDescription.addSubDescription(new AtomDescription("N"));
        groupDescription.addSubDescription(new AtomDescription("CA"));
        groupDescription.addSubDescription(new AtomDescription("O"));
        
        Group group = new Group();
        addAtom(group, "N");
        addAtom(group, "CA");
        addAtom(group, "O");
        
        LeafEngine engine = new LeafEngine();
        List<Match> matches = engine.match(groupDescription, group); 
        for (Match match : matches) {
            System.out.println(match);
        }
        
        assertEquals(groupDescription.size(), matches.size());
    }

}
