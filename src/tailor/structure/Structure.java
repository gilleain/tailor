package tailor.structure;

import java.util.List;

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
    
    /**
     * TODO - not sure about this...
     * 
     * @return
     */
    public String getName();


    // TODO XXX  should we allow external iteration?
    public List<Structure> getSubstructures();
    
    public void addSubStructure(Structure structure);
    
    /**
     * Get a property for a key.
     * 
     * @param string
     * @return
     */
    public String getProperty(String key);
    
    public void copyProperty(Structure other, String key);

    public boolean hasPropertyEqualTo(String string, String name);

    public void setProperty(String string, String string2);


}
