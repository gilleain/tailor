package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.datasource.Structure;
import tailor.description.Description;

/**
 * Associates a description to a structure. 
 * 
 * @author maclean
 *
 */
public class Association {
    
    private Description description;
    
    private Structure structure;
    
    private List<Association> subAssociations;
    
    public Association(Description description, Structure structure) {
        this.description = description;
        this.structure = structure;
        this.subAssociations = new ArrayList<Association>();
    }
    
    /**
     * Make a sub-association between a sub-description and a sub-structure.
     * Returns a reference to the newly created association.
     *  
     * @param description
     * @param structure
     */
    public Association associate(Description description, Structure structure) {
        Association childAssociation = new Association(description, structure);
        addSubAssociation(childAssociation);
        return childAssociation;
    }
    
    public void addSubAssociation(Association association) {
        subAssociations.add(association);
    }
    
    public Description getDescription() {
        return this.description;
    }
    
    public Structure getStructure() {
        return this.structure;
    }

}
