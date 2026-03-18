package tailor.measurement;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;

/**
 * Tests for TorsionMeasure.
 *
 * Note: calculate() and measure() are not tested because GeometricMeasure.getPoint()
 * is currently an unimplemented stub that returns null.
 */
public class TestTorsionMeasure {

    private Description a = new AtomDescription("N");
    private Description b = new AtomDescription("CA");
    private Description c = new AtomDescription("C");
    private Description d = new AtomDescription("N");

    @Test
    public void getNameReturnsConstructorName() {
        TorsionMeasure tm = new TorsionMeasure("phi", a, b, c, d);
        assertEquals("phi", tm.getName());
    }

    @Test
    public void defaultConstructorUsesTorsionAsName() {
        TorsionMeasure tm = new TorsionMeasure(a, b, c, d);
        assertEquals("Torsion", tm.getName());
    }

    @Test
    public void getDescriptionAReturnsFirstAtom() {
        TorsionMeasure tm = new TorsionMeasure("phi", a, b, c, d);
        assertSame(a, tm.getDescriptionA());
    }

    @Test
    public void getDescriptionBReturnsSecondAtom() {
        TorsionMeasure tm = new TorsionMeasure("phi", a, b, c, d);
        assertSame(b, tm.getDescriptionB());
    }

    @Test
    public void getDescriptionCReturnsThirdAtom() {
        TorsionMeasure tm = new TorsionMeasure("phi", a, b, c, d);
        assertSame(c, tm.getDescriptionC());
    }

    @Test
    public void getDescriptionDReturnsFourthAtom() {
        TorsionMeasure tm = new TorsionMeasure("phi", a, b, c, d);
        assertSame(d, tm.getDescriptionD());
    }

    @Test
    public void getNumberOfColumnsIsOne() {
        TorsionMeasure tm = new TorsionMeasure("phi", a, b, c, d);
        assertEquals(1, tm.getNumberOfColumns());
    }

    @Test
    public void getColumnHeadersReturnsMeasureName() {
        TorsionMeasure tm = new TorsionMeasure("phi", a, b, c, d);
        assertArrayEquals(new String[]{"phi"}, tm.getColumnHeaders());
    }

    @Test
    public void getColumnHeadersWithNullNameReturnsTorsion() {
        TorsionMeasure tm = new TorsionMeasure(null, a, b, c, d);
        assertArrayEquals(new String[]{"Torsion"}, tm.getColumnHeaders());
    }

    @Test
    public void getFormatStringsReturnsTwoDecimalFloat() {
        TorsionMeasure tm = new TorsionMeasure("phi", a, b, c, d);
        assertArrayEquals(new String[]{"%.2f"}, tm.getFormatStrings());
    }

    @Test
    public void toStringContainsAtomNames() {
        TorsionMeasure tm = new TorsionMeasure("phi", a, b, c, d);
        String s = tm.toString();
        assertTrue(s.contains("N"));
        assertTrue(s.contains("CA"));
        assertTrue(s.contains("C"));
    }
}
