package tailor.structure;

import java.util.ArrayList;
import java.util.List;

public class Helix implements Structure, SSE {
    
    private final List<Group> groups;
    
    public Helix() {
        this.groups = new ArrayList<Group>();
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

}
