package tailor.structure;

import java.util.ArrayList;
import java.util.List;

import tailor.description.Description;

/**
 * Selects atoms from a structure based on a description.
 * 
 * @author maclean
 *
 */
public class AtomSelector implements StructureVisitor {
    
    /**
     * The root of the subtree we are selecting
     */
    private Description root;
    
    /**
     * The current position in the description
     */
    private Description current;
    
    /**
     * The selected list of atoms (if any)
     */
    private List<Atom> selected;

    public AtomSelector(Description description) {
        this.root = description;
        this.current = description;
        this.selected = new ArrayList<Atom>();
    }

    @Override
    public void visit(Structure structure) {
        System.out.println("Visiting "+ structure.getLevel() + " " + structure.getName() );
        if (levelMatches(current, structure) && matches(current, structure)) {
            List<? extends Description> subDescriptions = current.getSubDescriptions(); 
            if (subDescriptions.size() > 0) {
                current = current.getSubDescriptionAt(0);
            } else {
                if (structure.getLevel() == Level.ATOM) {
                    selected.add((Atom) structure);
                }
                // XXX - no tree!
            }
        }
    }
    
    private boolean levelMatches(Description description, Structure structure) {
        return description.getLevel() == structure.getLevel();
    }
    
    private boolean matches(Description description, Structure structure) {
        return description.getName() == null 
                || description.getName().equals(structure.getName());
    }
    
    public List<Atom> get() {
        return selected;
    }

}
