package tailor.engine.execute;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Group;

public class TestFixedAtomSelector {
    
    @Test
    public void testGet() {
        Vector targetP = new Vector(1, 1, 1);
        Vector notExpected = new Vector(2, 2, 2);
        
        AtomDescription atomDescription = new AtomDescription("C");
        Group group = new Group();
        group.addAtom(new Atom("N", notExpected));
        group.addAtom(new Atom("C", targetP));
        
        FixedAtomCenterSelector selector = new FixedAtomCenterSelector(atomDescription, group);
        Vector p = selector.get();
        assertEquals(targetP, p);
    }

}
