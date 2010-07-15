package tailor.engine;

import org.junit.Test;

import tailor.Level;
import tailor.datasource.Structure;
import tailor.description.AtomDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;

public class LeafEngineTest {
    
    public void addAtom(Structure group, String name) {
        Structure atom = new Structure(Level.ATOM);
        atom.setProperty("Name", name);
        group.addSubStructure(atom);
    }
    
    @Test
    public void simpleTest() {
        Description groupDescription = new GroupDescription();
        groupDescription.addSubDescription(new AtomDescription("N"));
        groupDescription.addSubDescription(new AtomDescription("CA"));
        groupDescription.addSubDescription(new AtomDescription("O"));
        
        Structure group = new Structure();
        addAtom(group, "N");
        addAtom(group, "CA");
        addAtom(group, "O");
        
        LeafEngine engine = new LeafEngine();
        for (Match match : engine.match(groupDescription, group)) {
            System.out.println(match);
        }
    }

}