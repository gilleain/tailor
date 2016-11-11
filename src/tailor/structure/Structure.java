package tailor.structure;

/**
 * Structures are hierarchical elements with substructures.
 * 
 * @author maclean
 *
 */
public interface Structure {
    
    /**
     * Accept a visitor.
     * 
     * @param visitor
     */
    public void accept(StructureVisitor visitor);
    
    /**
     * Accept a hierarchy visitor.
     * 
     * @param visitor
     */
    public void accept(HierarchyVisitor visitor);
    
    /**
     * @return the specific level of this structure
     */
    public Level getLevel();

}
