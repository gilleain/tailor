package tailor.structure;

import java.util.ArrayList;
import java.util.List;

public class Chain implements Structure {
    
    private final Level level = Level.CHAIN;
    
    private final List<Group> groups;
    
    private final List<SSE> sses;
    
    public Chain() {
        this.groups = new ArrayList<Group>();
        this.sses = new ArrayList<SSE>();
    }
    
    public void addSSE(SSE sse) {
        this.sses.add(sse);
    }
    
    public List<SSE> getSSEs() {
        return this.sses;
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

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

}
