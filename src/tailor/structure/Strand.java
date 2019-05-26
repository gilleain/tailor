package tailor.structure;

import java.util.ArrayList;

public class Strand extends Segment implements Structure, SSE {
    
    public Strand() {
        super(new ArrayList<>());
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
        super.addGroup(group);
    }

    @Override
    public Group getFirst() {
        return super.getFirst();
    }

    @Override
    public Group getLast() {
        return super.getLast();
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addSubStructure(Structure structure) {
        if (structure instanceof Group) {
            addGroup((Group) structure);
        } else {
            throw new IllegalArgumentException("Can only add instances of " + Group.class.getName());
        }
    }
}
