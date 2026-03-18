package tailor.measurement;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestHBondMeasurement {

    private static final double DELTA = 1e-9;

    @Test
    public void getHaDistanceReturnsConstructorValue() {
        HBondMeasurement m = new HBondMeasurement(2.1, 150.0, 110.0);
        assertEquals(2.1, m.getHaDistance(), DELTA);
    }

    @Test
    public void getDhaAngleReturnsConstructorValue() {
        HBondMeasurement m = new HBondMeasurement(2.1, 150.0, 110.0);
        assertEquals(150.0, m.getDhaAngle(), DELTA);
    }

    @Test
    public void getHaaAngleReturnsConstructorValue() {
        HBondMeasurement m = new HBondMeasurement(2.1, 150.0, 110.0);
        assertEquals(110.0, m.getHaaAngle(), DELTA);
    }

    @Test
    public void toStringIsNonEmpty() {
        HBondMeasurement m = new HBondMeasurement(2.1, 150.0, 110.0);
        assertEquals("HBond Measurement", m.toString());
    }
}
