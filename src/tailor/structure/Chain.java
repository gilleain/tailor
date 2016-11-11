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
    public void visit(StructureVisitor visitor) {
        visitor.visit(this);
        for (Group group : groups) {
            group.visit(visitor);
        }
    }

    @Override
    public Level getLevel() {
        return level;
    }

}
