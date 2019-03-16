package tailor.engine.execute;

import java.util.ArrayList;
import java.util.List;

import tailor.structure.Atom;
import tailor.structure.Group;

public class StructureBuilder {
    
    private List<Group> groups;
    
    private Group currentGroup;
    
    private StructureBuilder() {
        this.groups = new ArrayList<>();
    }
    
    public static StructureBuilder makeStructure() {
        return new StructureBuilder();
    }
    
    public StructureBuilder group(String groupName) {
        currentGroup = new Group();
        groups.add(currentGroup);
        currentGroup.setId(groupName);
        return this;
    }
    
    public StructureBuilder atoms(String... atomNameArr) {
        for (String atomName : atomNameArr) {
            Atom atom = new Atom(atomName);
            currentGroup.addAtom(atom);
        }
        return this;
    }
    
    public List<Group> get() {
        return groups;
    }

}
