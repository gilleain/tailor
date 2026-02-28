package tailor.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;

/**
 * Tests for AngleBoundCondition.
 *
 * Note: satisfiedBy() is not tested here because GeometricMeasure.getPoint()
 * is currently an unimplemented stub that returns null.
 */
public class TestAngleBoundCondition {

    private AngleBoundCondition makeCondition(
            String name, double center, double range) {
        Description a = new AtomDescription("N");
        Description b = new AtomDescription("CA");
        Description c = new AtomDescription("C");
        return new AngleBoundCondition(name, a, b, c, center, range);
    }

    @Test
    public void toStringContainsNameAndBounds() {
        AngleBoundCondition cond = makeCondition("angle", 120.0, 20.0);
        String s = cond.toString();
        assertTrue(s.contains("angle"));
        // lower = 100.0, upper = 140.0
        assertTrue(s.contains("100.0"));
        assertTrue(s.contains("140.0"));
    }

    @Test
    public void negateMarksConditionAsNegated() {
        AngleBoundCondition cond = makeCondition("angle", 120.0, 20.0);
        assertFalse(cond.isNegated());
        cond.negate();
        assertTrue(cond.isNegated());
    }

    @Test
    public void containsReturnsFalse() {
        AngleBoundCondition cond = makeCondition("angle", 120.0, 20.0);
        assertFalse(cond.contains(new AtomDescription("CA")));
    }

    @Test
    public void toXmlReturnsEmptyString() {
        AngleBoundCondition cond = makeCondition("angle", 120.0, 20.0);
        assertEquals("", cond.toXml());
    }

    @Test
    public void equalsTrueForSameInstance() {
        AngleBoundCondition cond = makeCondition("angle", 120.0, 20.0);
        assertTrue(cond.equals(cond));
    }

    @Test
    public void equalsFalseForNull() {
        AngleBoundCondition cond = makeCondition("angle", 120.0, 20.0);
        assertFalse(cond.equals(null));
    }

    @Test
    public void equalsFalseForDifferentType() {
        AngleBoundCondition cond = makeCondition("angle", 120.0, 20.0);
        assertFalse(cond.equals("angle"));
    }
}
