package tailor.structure;

import java.util.ArrayList;
import java.util.List;

public class Chain implements Structure {
    
    private final Level level = Level.CHAIN;
    
    private final List<Group> groups;
    
    private final List<SSE> sses;
    
    private final String name;
    
    public Chain() {
        this("A");  // XXX is this a sensible default?
    }
    
    public Chain(String name) {
        this.name = name;
        this.groups = new ArrayList<>();
        this.sses = new ArrayList<>();
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
        return name;
    }

    public List<Group> getGroups() {
        return this.groups;
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
        List<Structure> substructures = new ArrayList<>();
        substructures.addAll(groups);
        return substructures;
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
