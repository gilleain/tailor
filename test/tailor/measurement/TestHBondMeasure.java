package tailor.measurement;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;

/**
 * Tests for HBondMeasure.
 *
 * Note: measure() is not tested because GeometricMeasure.getPoint()
 * is currently an unimplemented stub that returns null.
 */
public class TestHBondMeasure {

    private final Description donor    = new AtomDescription("N");
    private final Description hydrogen = new AtomDescription("H");
    private final Description acceptor = new AtomDescription("O");
    private final Description attached = new AtomDescription("C");

    private HBondMeasure make() {
        return new HBondMeasure("hbond", donor, hydrogen, acceptor, attached);
    }

    @Test
    public void getNameReturnsConstructorName() {
        assertEquals("hbond", make().getName());
    }

    @Test
    public void getDonorAtomDescriptionReturnsConstructorValue() {
        assertSame(donor, make().getDonorAtomDescription());
    }

    @Test
    public void getHydrogenAtomDescriptionReturnsConstructorValue() {
        assertSame(hydrogen, make().getHydrogenAtomDescription());
    }

    @Test
    public void getAcceptorAtomDescriptionReturnsConstructorValue() {
        assertSame(acceptor, make().getAcceptorAtomDescription());
    }

    @Test
    public void getAttachedAtomDescriptionReturnsConstructorValue() {
        assertSame(attached, make().getAttachedAtomDescription());
    }

    @Test
    public void containsIsTrueForDonorDescription() {
        assertTrue(make().contains(donor));
    }

    @Test
    public void containsIsTrueForHydrogenDescription() {
        assertTrue(make().contains(hydrogen));
    }

    @Test
    public void containsIsTrueForAcceptorDescription() {
        assertTrue(make().contains(acceptor));
    }

    @Test
    public void containsIsTrueForAttachedDescription() {
        assertTrue(make().contains(attached));
    }

    @Test
    public void containsIsFalseForUnrelatedDescription() {
        assertFalse(make().contains(new AtomDescription("CB")));
    }

    @Test
    public void getNumberOfColumnsIsThree() {
        assertEquals(3, make().getNumberOfColumns());
    }

    @Test
    public void getColumnHeadersHaveThreeElements() {
        assertEquals(3, make().getColumnHeaders().length);
    }

    @Test
    public void getColumnHeadersContainNameAsPrefix() {
        String[] headers = make().getColumnHeaders();
        assertTrue(headers[0].startsWith("hbond"));
        assertTrue(headers[1].startsWith("hbond"));
        assertTrue(headers[2].startsWith("hbond"));
    }

    @Test
    public void getColumnHeadersEncodeDistanceAndAngles() {
        String[] headers = make().getColumnHeaders();
        assertTrue(headers[0].contains("D_H"));
        assertTrue(headers[1].contains("DHA"));
        assertTrue(headers[2].contains("HAA"));
    }

    @Test
    public void getColumnHeadersWithNullNameFallBackToHBond() {
        HBondMeasure hm = new HBondMeasure(null, donor, hydrogen, acceptor, attached);
        String[] headers = hm.getColumnHeaders();
        assertTrue(headers[0].startsWith("HBond"));
    }

    @Test
    public void getFormatStringsHaveThreeElements() {
        assertArrayEquals(new String[]{"%.2f", "%.2f", "%.2f"}, make().getFormatStrings());
    }

    @Test
    public void toStringContainsHB() {
        assertTrue(make().toString().startsWith("HB("));
    }
}
