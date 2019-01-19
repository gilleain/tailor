package tailor.structure;

import java.util.ArrayList;
import java.util.List;

public class Strand implements Structure, SSE {
    
    private final List<Group> groups;
    
    public Strand() {
        this.groups = new ArrayList<>();
    }

    @Override
    public void accept(StructureVisitor visitor) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void accept(HierarchyVisitor visitor) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Level getLevel() {
        return Level.SSE;
    }
    
    @Override
    public void addGroup(Group group) {
        groups.add(group);
    }

    @Override
    public Group getFirst() {
        return groups.get(0);
    }

    @Override
    public Group getLast() {
        return groups.get(groups.size() - 1);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addSubStructure(Structure structure) {
        if (structure instanceof Group) {
            groups.add((Group) structure);
        } else {
            throw new IllegalArgumentException("Can only add instances of " + Group.class.getName());
        }
    }

    @Override
    public List<Structure> getSubstructures() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProperty(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void copyProperty(Structure other, String key) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean hasPropertyEqualTo(String string, String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setProperty(String string, String string2) {
        // TODO Auto-generated method stub
        
    }

}
