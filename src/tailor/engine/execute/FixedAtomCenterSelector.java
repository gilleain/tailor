package tailor.engine.execute;

import tailor.description.AtomDescription;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Group;

/**
 * Provide the atom centre of one particular atom.
 * 
 * @author gilleain
 *
 */
public class FixedAtomCenterSelector implements Selector<Vector, Void> {
    
    private final Vector point;
    
    public FixedAtomCenterSelector(AtomDescription description, Group group) {
        this.point = resolve(description, group);
    }

    private Vector resolve(AtomDescription description, Group group) {
        for (Atom atom : group.getAtoms()) {
            if (atom.getName().equals(description.getName())) {
                return atom.getCenter();
            }
        }
        return null;    // TODO - throw some 'resolution error'?
    }

    @Override
    public Vector get(Void... entities) {
        return point;
    }

}
