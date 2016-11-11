package tailor.structure;

public interface HierarchyVisitor {
    
    public void enter(Structure structure);
    
    public void exit(Structure structure);
    
    public void visit(Structure structure);

}
