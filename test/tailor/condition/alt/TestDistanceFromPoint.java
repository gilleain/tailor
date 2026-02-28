package tailor.condition.alt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tailor.engine.execute.Selector;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Group;

public class TestDistanceFromPoint {

    /** Fixed point selector that always returns the given vector. */
    private static Selector<Vector, Void> fixedPoint(double x, double y, double z) {
        return voids -> new Vector(x, y, z);
    }

    /** Variable point selector that returns the CA atom position from a group. */
    private static Selector<Vector, Group> variablePoint() {
        return groups -> groups[0].getAtomPosition("CA");
    }

    private static Group groupWithCA(double x, double y, double z) {
        Group group = new Group();
        group.addAtom(new Atom("CA", new Vector(x, y, z)));
        return group;
    }

    @Test
    public void arityIsOne() {
        DistanceFromPoint cond = new DistanceFromPoint(5.0, fixedPoint(0, 0, 0), variablePoint());
        assertEquals(1, cond.arity());
    }

    @Test
    public void allowsWhenGroupIsWithinMinDistance() {
        // Fixed at origin; CA at (2,0,0) → distance=2, min=5 → allows
        DistanceFromPoint cond = new DistanceFromPoint(5.0, fixedPoint(0, 0, 0), variablePoint());
        Group group = groupWithCA(2, 0, 0);
        assertTrue(cond.allows(group));
    }

    @Test
    public void disallowsWhenGroupExceedsMinDistance() {
        // Fixed at origin; CA at (10,0,0) → distance=10, min=5 → disallows
        DistanceFromPoint cond = new DistanceFromPoint(5.0, fixedPoint(0, 0, 0), variablePoint());
        Group group = groupWithCA(10, 0, 0);
        assertFalse(cond.allows(group));
    }

    @Test
    public void disallowsWhenDistanceEqualsMinDistance() {
        // Fixed at origin; CA at (5,0,0) → distance=5, min=5 → d < min is false
        DistanceFromPoint cond = new DistanceFromPoint(5.0, fixedPoint(0, 0, 0), variablePoint());
        Group group = groupWithCA(5, 0, 0);
        assertFalse(cond.allows(group));
    }

    @Test
    public void disallowsWhenVariablePointSelectorReturnsNull() {
        // Group has no CA atom → getAtomPosition returns null
        DistanceFromPoint cond = new DistanceFromPoint(5.0, fixedPoint(0, 0, 0), variablePoint());
        Group group = new Group();
        assertFalse(cond.allows(group));
    }

    @Test
    public void worksWithNonOriginFixedPoint() {
        // Fixed at (1,0,0); CA at (4,0,0) → distance=3, min=5 → allows
        DistanceFromPoint cond = new DistanceFromPoint(5.0, fixedPoint(1, 0, 0), variablePoint());
        Group group = groupWithCA(4, 0, 0);
        assertTrue(cond.allows(group));
    }

    @Test
    public void worksWith3DDistance() {
        // Fixed at origin; CA at (3,4,0) → distance=5, min=6 → allows
        DistanceFromPoint cond = new DistanceFromPoint(6.0, fixedPoint(0, 0, 0), variablePoint());
        Group group = groupWithCA(3, 4, 0);
        assertTrue(cond.allows(group));
    }
}
