package tailor.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * A segment is a sequence of groups, for example a Chain or an SSE.
 * 
 * @author gilleain
 *
 */
public abstract class Segment implements Structure {
    
    private final List<Group> groups;
    
    public Segment(List<Group> groups) {
        this.groups = groups;
    }
    
    public void addGroup(Group group) {
        groups.add(group);
    }
    
    public Group getFirst() {
        return groups.get(0);
    }
    
    public Group getLast() {
        return groups.get(groups.size() - 1);
    }
    
    public List<Group> getGroups() {
        return this.groups;
    }
    
    @Override
    public List<Structure> getSubstructures() {
        List<Structure> substructures = new ArrayList<>();
        substructures.addAll(groups);
        return substructures;
    }
    
    @Override
    public void accept(StructureVisitor visitor) {
        visitor.visit(this);
        for (Group group : groups) {
            group.accept(visitor);
        }
    }
    
    @Override
    public void accept(HierarchyVisitor visitor) {
        visitor.enter(this);
        for (Group group : groups) {
            group.accept(visitor);
        }
        visitor.exit(this);
    }

}
