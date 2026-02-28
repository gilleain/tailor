package tailor.condition.alt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tailor.engine.execute.Selector;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Group;

public class TestDistanceBetween {

    /** A selector that returns the position of the named atom from the group. */
    private static Selector<Vector, Group> atomSelector(String atomName) {
        return groups -> groups[0].getAtomPosition(atomName);
    }

    private static Group groupWithAtom(String name, double x, double y, double z) {
        Group group = new Group();
        group.addAtom(new Atom(name, new Vector(x, y, z)));
        return group;
    }

    @Test
    public void arityIsTwo() {
        DistanceBetween cond = new DistanceBetween(5.0, atomSelector("CA"), atomSelector("CA"));
        assertEquals(2, cond.arity());
    }

    @Test
    public void allowsWhenDistanceIsLessThanMin() {
        // Two atoms 2 Å apart; min distance = 5.0 → should allow
        Group groupA = groupWithAtom("CA", 0, 0, 0);
        Group groupB = groupWithAtom("CA", 2, 0, 0);
        DistanceBetween cond = new DistanceBetween(5.0, atomSelector("CA"), atomSelector("CA"));
        assertTrue(cond.allows(groupA, groupB));
    }

    @Test
    public void disallowsWhenDistanceExceedsMin() {
        // Two atoms 10 Å apart; min distance = 5.0 → should not allow
        Group groupA = groupWithAtom("CA", 0, 0, 0);
        Group groupB = groupWithAtom("CA", 10, 0, 0);
        DistanceBetween cond = new DistanceBetween(5.0, atomSelector("CA"), atomSelector("CA"));
        assertFalse(cond.allows(groupA, groupB));
    }

    @Test
    public void disallowsWhenDistanceEqualsMin() {
        // Two atoms exactly 5 Å apart; min distance = 5.0 → distance < min is false
        Group groupA = groupWithAtom("CA", 0, 0, 0);
        Group groupB = groupWithAtom("CA", 5, 0, 0);
        DistanceBetween cond = new DistanceBetween(5.0, atomSelector("CA"), atomSelector("CA"));
        assertFalse(cond.allows(groupA, groupB));
    }

    @Test
    public void disallowsWhenSelectorReturnsNullForFirstGroup() {
        // groupA has no "CA" atom → getAtomPosition returns null
        Group groupA = new Group();
        Group groupB = groupWithAtom("CA", 0, 0, 0);
        DistanceBetween cond = new DistanceBetween(5.0, atomSelector("CA"), atomSelector("CA"));
        assertFalse(cond.allows(groupA, groupB));
    }

    @Test
    public void disallowsWhenSelectorReturnsNullForSecondGroup() {
        Group groupA = groupWithAtom("CA", 0, 0, 0);
        Group groupB = new Group();
        DistanceBetween cond = new DistanceBetween(5.0, atomSelector("CA"), atomSelector("CA"));
        assertFalse(cond.allows(groupA, groupB));
    }

    @Test
    public void worksWithDiagonalDistance() {
        // Two atoms at (0,0,0) and (3,4,0) → distance = 5
        Group groupA = groupWithAtom("CA", 0, 0, 0);
        Group groupB = groupWithAtom("CA", 3, 4, 0);
        DistanceBetween inside = new DistanceBetween(6.0, atomSelector("CA"), atomSelector("CA"));
        DistanceBetween outside = new DistanceBetween(4.0, atomSelector("CA"), atomSelector("CA"));
        assertTrue(inside.allows(groupA, groupB));
        assertFalse(outside.allows(groupA, groupB));
    }
}
