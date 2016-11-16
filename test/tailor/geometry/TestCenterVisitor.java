package tailor.geometry;

import org.junit.Test;

import tailor.structure.Atom;
import tailor.structure.Group;

public class TestCenterVisitor {
    
    @Test
    public void testResidue() {
        Group group = new Group();
        Atom a1 = new Atom("A", new Vector(1, 1, 1));
        Atom a2 = new Atom("B", new Vector(1, 2, 1));
        Atom a3 = new Atom("C", new Vector(1, 3, 2));
        group.addAtom(a1);
        group.addAtom(a2);
        group.addAtom(a3);
        CenterVisitor visitor = new CenterVisitor();
        group.accept(visitor);
        System.out.println(visitor.get());
    }

}
