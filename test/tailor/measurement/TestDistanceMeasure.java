package tailor.measurement;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;

/**
 * Tests for DistanceMeasure.
 *
 * Note: calculate() and measure() are not tested because GeometricMeasure.getPoint()
 * is currently an unimplemented stub that returns null.
 */
public class TestDistanceMeasure {

    private Description a = new AtomDescription("O");
    private Description b = new AtomDescription("N");

    @Test
    public void getNameReturnsConstructorName() {
        DistanceMeasure dm = new DistanceMeasure("O_N", a, b);
        assertEquals("O_N", dm.getName());
    }

    @Test
    public void getNameWithNullReturnsNull() {
        DistanceMeasure dm = new DistanceMeasure(null, a, b);
        assertEquals(null, dm.getName());
    }

    @Test
    public void getDescriptionAReturnsFirstAtom() {
        DistanceMeasure dm = new DistanceMeasure("d", a, b);
        assertSame(a, dm.getDescriptionA());
    }

    @Test
    public void getDescriptionBReturnsSecondAtom() {
        DistanceMeasure dm = new DistanceMeasure("d", a, b);
        assertSame(b, dm.getDescriptionB());
    }

    @Test
    public void getNumberOfColumnsIsOne() {
        DistanceMeasure dm = new DistanceMeasure("d", a, b);
        assertEquals(1, dm.getNumberOfColumns());
    }

    @Test
    public void getColumnHeadersReturnsMeasureName() {
        DistanceMeasure dm = new DistanceMeasure("O_N", a, b);
        assertArrayEquals(new String[]{"O_N"}, dm.getColumnHeaders());
    }

    @Test
    public void getColumnHeadersWithNullNameReturnsDistance() {
        DistanceMeasure dm = new DistanceMeasure(null, a, b);
        assertArrayEquals(new String[]{"Distance"}, dm.getColumnHeaders());
    }

    @Test
    public void getFormatStringsReturnsTwoDecimalFloat() {
        DistanceMeasure dm = new DistanceMeasure("d", a, b);
        assertArrayEquals(new String[]{"%.2f"}, dm.getFormatStrings());
    }
}
