package tailor.structure;

import java.util.ArrayList;
import java.util.List;

public class Chain extends Segment implements Structure {
    
    private final Level level = Level.CHAIN;
    
    private final List<SSE> sses;
    
    private String name;
    
    public void setName(String name) {
        this.name = name;
    }


    private ChainType type;
    
    public Chain() {
        this("A");  // XXX is this a sensible default?
    }
    
    public Chain(String name) {	// TODO - type is null here 
        super(new ArrayList<>());
        this.name = name;
        this.sses = new ArrayList<>();
    }
    
    public Chain(String name, ChainType type) {
        super(new ArrayList<>());
        this.name = name;
        this.sses = new ArrayList<>();
        this.type = type;
    }
    
    public ChainType getType() {
        return this.type;
    }
    
    public String getResidueRange() {
        if (size() > 1) {
            return String.format("%s-%s", getFirst().getNumber(), getLast().getNumber());
        } else {
            return String.valueOf(getFirst().getNumber());
        }
    }
    
    public Group getGroupAt(int index) {
    	return this.getGroups().get(index);	// TODO - is this the best way to do this?
    }
    
    public void setType(ChainType type) {
        this.type = type;
    }
    
    public void addSSE(SSE sse) {
        this.sses.add(sse);
    }
    
    public List<SSE> getSSEs() {
        return this.sses;
    }
    
    public void addGroup(Group group) {
        super.addGroup(group);
    }

    @Override
    public void accept(StructureVisitor visitor) {
        super.accept(visitor);
    }
    
    @Override
    public void accept(HierarchyVisitor visitor) {
       super.accept(visitor);
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public void addSubStructure(Structure structure) {
        if (structure instanceof Group) {
            super.addGroup((Group) structure);
        } else {
            throw new IllegalArgumentException("Can only add instances of " + Group.class.getName());
        }
    }

}
