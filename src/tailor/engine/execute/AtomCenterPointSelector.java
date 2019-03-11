package tailor.engine.execute;

import tailor.description.AtomDescription;
import tailor.description.GroupDescription;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Group;

/**
 * Provide the atom center of the atom matched in any group passed in
 * 
 * @author gilleain
 *
 */
public class AtomCenterPointSelector implements Selector<Vector, Group> {
    
    private final GroupDescription description;
    
    public AtomCenterPointSelector(GroupDescription description) {
        this.description = description;
    }
    
    private Vector resolve(GroupDescription description, Group group) {
        if (group.getName().equals(description.getName())) {
            AtomDescription atomDescription = description.getAtomDescriptions().get(0); // XXX
            for (Atom atom : group.getAtoms()) {
                if (atom.getName().equals(atomDescription.getName())) {
                    return atom.getCenter();
                }
            }
        }
        return null;    // TODO - throw some 'resolution error'?
    }

    @Override
    public Vector get(Group... groups) {
        return resolve(description, groups[0]);
    }

}
