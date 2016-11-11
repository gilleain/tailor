package tailor.structure;

import java.util.ArrayList;
import java.util.List;

public class Chain implements Structure {
    
    private final Level level = Level.CHAIN;
    
    private final List<Group> groups;
    
    public Chain() {
        this.groups = new ArrayList<Group>();
    }
    
    public void addGroup(Group group) {
        this.groups.add(group);
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

    @Override
    public Level getLevel() {
        return level;
    }

}
