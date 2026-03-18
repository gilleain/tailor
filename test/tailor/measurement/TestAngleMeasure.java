package tailor.measurement;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;

/**
 * Tests for AngleMeasure.
 *
 * Note: calculate() and measure() are not tested because GeometricMeasure.getPoint()
 * is currently an unimplemented stub that returns null.
 */
public class TestAngleMeasure {

    private Description a = new AtomDescription("N");
    private Description b = new AtomDescription("CA");
    private Description c = new AtomDescription("C");

    @Test
    public void getNameReturnsConstructorName() {
        AngleMeasure am = new AngleMeasure("NCAangle", a, b, c);
        assertEquals("NCAangle", am.getName());
    }

    @Test
    public void defaultConstructorUsesAngleAsName() {
        AngleMeasure am = new AngleMeasure(a, b, c);
        assertEquals("Angle", am.getName());
    }

    @Test
    public void getNumberOfColumnsIsOne() {
        AngleMeasure am = new AngleMeasure("theta", a, b, c);
        assertEquals(1, am.getNumberOfColumns());
    }

    @Test
    public void getColumnHeadersReturnsMeasureName() {
        AngleMeasure am = new AngleMeasure("NCAangle", a, b, c);
        assertArrayEquals(new String[]{"NCAangle"}, am.getColumnHeaders());
    }

    @Test
    public void getColumnHeadersWithNullNameReturnsAngle() {
        AngleMeasure am = new AngleMeasure(null, a, b, c);
        assertArrayEquals(new String[]{"Angle"}, am.getColumnHeaders());
    }

    @Test
    public void getFormatStringsReturnsTwoDecimalFloat() {
        AngleMeasure am = new AngleMeasure("theta", a, b, c);
        assertArrayEquals(new String[]{"%.2f"}, am.getFormatStrings());
    }
}
