package tailor.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;

/**
 * Tests for TorsionBoundCondition.
 *
 * Note: satisfiedBy() is not tested here because GeometricMeasure.getPoint()
 * is currently an unimplemented stub that returns null.
 */
public class TestTorsionBoundCondition {

    private static final double DELTA = 1e-9;

    private TorsionBoundCondition makeCondition(
            String name, double midPoint, double range) {
        Description a = new AtomDescription("N");
        Description b = new AtomDescription("CA");
        Description c = new AtomDescription("C");
        Description d = new AtomDescription("N");
        return new TorsionBoundCondition(name, a, b, c, d, midPoint, range);
    }

    @Test
    public void getNameReturnsConstructorName() {
        TorsionBoundCondition cond = makeCondition("phi", -60, 30);
        assertEquals("phi", cond.getName());
    }

    @Test
    public void getMidPointReturnsConstructorValue() {
        TorsionBoundCondition cond = makeCondition("phi", -60, 30);
        assertEquals(-60.0, cond.getMidPoint(), DELTA);
    }

    @Test
    public void getRangeReturnsConstructorValue() {
        TorsionBoundCondition cond = makeCondition("phi", -60, 30);
        assertEquals(30.0, cond.getRange(), DELTA);
    }

    @Test
    public void getDescriptionsReturnConstructorValues() {
        Description a = new AtomDescription("N");
        Description b = new AtomDescription("CA");
        Description c = new AtomDescription("C");
        Description d = new AtomDescription("N");
        TorsionBoundCondition cond =
                new TorsionBoundCondition("phi", a, b, c, d, -60, 30);

        assertSame(a, cond.getDescriptionA());
        assertSame(b, cond.getDescriptionB());
        assertSame(c, cond.getDescriptionC());
        assertSame(d, cond.getDescriptionD());
    }

    @Test
    public void letterSymbolDefaultsToEmptyString() {
        TorsionBoundCondition cond = makeCondition("phi", -60, 30);
        assertEquals("", cond.getLetterSymbol());
    }

    @Test
    public void setLetterSymbolIsRetrievable() {
        TorsionBoundCondition cond = makeCondition("phi", -60, 30);
        cond.setLetterSymbol("φ");
        assertEquals("φ", cond.getLetterSymbol());
    }

    @Test
    public void makeTorsionLabelShowsRange() {
        TorsionBoundCondition cond = makeCondition("phi", -60, 30);
        cond.setLetterSymbol("φ");
        // midPoint=−60, range=30 → lower=−90, upper=−30
        assertEquals("-90 < φ < -30", cond.makeTorsionLabel());
    }

    @Test
    public void toStringContainsNameAndBounds() {
        TorsionBoundCondition cond = makeCondition("phi", -60, 30);
        String s = cond.toString();
        assertTrue(s.contains("phi"));
        assertTrue(s.contains("-90.0"));
        assertTrue(s.contains("-30.0"));
    }

    @Test
    public void negateMarksConditionAsNegated() {
        TorsionBoundCondition cond = makeCondition("phi", -60, 30);
        assertFalse(cond.isNegated());
        cond.negate();
        assertTrue(cond.isNegated());
    }

    @Test
    public void containsReturnsFalse() {
        TorsionBoundCondition cond = makeCondition("phi", -60, 30);
        assertFalse(cond.contains(new AtomDescription("CA")));
    }
}
