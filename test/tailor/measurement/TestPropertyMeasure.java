package tailor.measurement;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;

public class TestPropertyMeasure {

    private static Description desc(String name) {
        return new AtomDescription(name);
    }

    @Test
    public void getNameReturnsPropertyName() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "residueName", String.class);
        assertEquals("residueName", pm.getName());
    }

    @Test
    public void getNumberOfColumnsIsOne() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "residueName", String.class);
        assertEquals(1, pm.getNumberOfColumns());
    }

    @Test
    public void getColumnHeadersReturnsPropertyName() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "residueName", String.class);
        assertArrayEquals(new String[]{"residueName"}, pm.getColumnHeaders());
    }

    @Test
    public void getFormatStringsForStringTypeIsPercentS() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "residueName", String.class);
        assertArrayEquals(new String[]{"%s"}, pm.getFormatStrings());
    }

    @Test
    public void getFormatStringsForIntTypeIsPercentD() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "index", int.class);
        assertArrayEquals(new String[]{"%d"}, pm.getFormatStrings());
    }

    @Test
    public void getFormatStringsForIntegerTypeIsPercentD() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "index", Integer.class);
        assertArrayEquals(new String[]{"%d"}, pm.getFormatStrings());
    }

    @Test
    public void getFormatStringsForDoubleTypeIsPercentF() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "bFactor", double.class);
        assertArrayEquals(new String[]{"%.2f"}, pm.getFormatStrings());
    }

    @Test
    public void getFormatStringsForDoubleBoxedTypeIsPercentF() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "bFactor", Double.class);
        assertArrayEquals(new String[]{"%.2f"}, pm.getFormatStrings());
    }

    @Test
    public void getFormatStringsForFloatTypeIsPercentF() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "score", float.class);
        assertArrayEquals(new String[]{"%.2f"}, pm.getFormatStrings());
    }

    @Test
    public void getFormatStringsForFloatBoxedTypeIsPercentF() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "score", Float.class);
        assertArrayEquals(new String[]{"%.2f"}, pm.getFormatStrings());
    }

    @Test
    public void measureReturnsPropertyMeasurement() {
        // measure() is a stub that returns PropertyMeasurement("") regardless of match
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "residueName", String.class);
        PropertyMeasurement result = pm.measure(null);
        assertNotNull(result);
        assertEquals("", result.getValue());
    }

    @Test
    public void toStringContainsPropertyName() {
        PropertyMeasure pm = new PropertyMeasure(desc("CA"), "residueName", String.class);
        String s = pm.toString();
        assertNotNull(s);
        assertEquals(true, s.contains("residueName"));
    }
}
