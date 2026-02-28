package tailor.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;

/**
 * Tests for DistanceBoundCondition.
 *
 * Note: satisfiedBy() is not tested here because GeometricMeasure.getPoint()
 * is currently an unimplemented stub that returns null.
 */
public class TestDistanceBoundCondition {

    private static final double DELTA = 1e-9;

    private DistanceBoundCondition makeCondition(
            String name, double center, double range) {
        Description a = new AtomDescription("O");
        Description b = new AtomDescription("N");
        return new DistanceBoundCondition(name, a, b, center, range);
    }

    @Test
    public void getDescriptionAReturnFirstDescription() {
        Description a = new AtomDescription("O");
        Description b = new AtomDescription("N");
        DistanceBoundCondition cond =
                new DistanceBoundCondition("d", a, b, 3.5, 1.0);
        assertSame(a, cond.getDescriptionA());
    }

    @Test
    public void getDescriptionBReturnsSecondDescription() {
        Description a = new AtomDescription("O");
        Description b = new AtomDescription("N");
        DistanceBoundCondition cond =
                new DistanceBoundCondition("d", a, b, 3.5, 1.0);
        assertSame(b, cond.getDescriptionB());
    }

    @Test
    public void toStringContainsNameAndBounds() {
        DistanceBoundCondition cond = makeCondition("d", 3.5, 1.0);
        String s = cond.toString();
        assertTrue(s.contains("d"));
        // lower = center - range = 2.5, upper = center + range = 4.5
        assertTrue(s.contains("2.5"));
        assertTrue(s.contains("4.5"));
    }

    @Test
    public void negateMarksConditionAsNegated() {
        DistanceBoundCondition cond = makeCondition("d", 3.5, 1.0);
        assertFalse(cond.isNegated());
        cond.negate();
        assertTrue(cond.isNegated());
    }

    @Test
    public void containsReturnsFalse() {
        DistanceBoundCondition cond = makeCondition("d", 3.5, 1.0);
        assertFalse(cond.contains(new AtomDescription("O")));
    }

    @Test
    public void toXmlReturnsEmptyString() {
        DistanceBoundCondition cond = makeCondition("d", 3.5, 1.0);
        assertEquals("", cond.toXml());
    }
}
