package tailor.measurement;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestPropertyMeasurement {

    @Test
    public void getValueReturnsConstructorString() {
        PropertyMeasurement m = new PropertyMeasurement("ALA");
        assertEquals("ALA", m.getValue());
    }

    @Test
    public void getValueWithEmptyString() {
        PropertyMeasurement m = new PropertyMeasurement("");
        assertEquals("", m.getValue());
    }

    @Test
    public void getValueWithNumericString() {
        PropertyMeasurement m = new PropertyMeasurement("42");
        assertEquals("42", m.getValue());
    }
}
