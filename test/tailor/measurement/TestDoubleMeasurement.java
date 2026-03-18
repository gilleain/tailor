package tailor.measurement;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestDoubleMeasurement {

    private static final double DELTA = 1e-9;

    @Test
    public void getValueReturnsConstructorValue() {
        DoubleMeasurement m = new DoubleMeasurement(3.14);
        assertEquals(3.14, m.getValue(), DELTA);
    }

    @Test
    public void getValueWithZero() {
        DoubleMeasurement m = new DoubleMeasurement(0.0);
        assertEquals(0.0, m.getValue(), DELTA);
    }

    @Test
    public void getValueWithNegative() {
        DoubleMeasurement m = new DoubleMeasurement(-60.0);
        assertEquals(-60.0, m.getValue(), DELTA);
    }

    @Test
    public void getValueWithLargeAngle() {
        DoubleMeasurement m = new DoubleMeasurement(179.99);
        assertEquals(179.99, m.getValue(), DELTA);
    }
}
