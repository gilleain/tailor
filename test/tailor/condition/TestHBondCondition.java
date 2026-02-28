package tailor.condition;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.Description;

/**
 * Tests for HBondCondition.
 *
 * Note: satisfiedBy() is not tested here because GeometricMeasure.getPoint()
 * is currently an unimplemented stub that returns null.
 */
public class TestHBondCondition {

    private final Description donor     = new AtomDescription("N");
    private final Description hydrogen  = new AtomDescription("H");
    private final Description acceptor  = new AtomDescription("O");
    private final Description attached  = new AtomDescription("C");

    private HBondCondition makeAnonymous() {
        return new HBondCondition(donor, hydrogen, acceptor, attached, 2.5, 120.0, 90.0);
    }

    private HBondCondition makeNamed(String name) {
        return new HBondCondition(name, donor, hydrogen, acceptor, attached, 2.5, 120.0, 90.0);
    }

    @Test
    public void anonymousConstructorDefaultsToHBondName() {
        HBondCondition cond = makeAnonymous();
        assertTrue(cond.toString().contains("HBond"));
    }

    @Test
    public void namedConstructorUsesSuppliedName() {
        HBondCondition cond = makeNamed("backbone_hbond");
        assertTrue(cond.toString().contains("backbone_hbond"));
    }

    @Test
    public void getDonorReturnsConstructorValue() {
        HBondCondition cond = makeAnonymous();
        assertSame(donor, cond.getDonorAtomDescription());
    }

    @Test
    public void getHydrogenReturnsConstructorValue() {
        HBondCondition cond = makeAnonymous();
        assertSame(hydrogen, cond.getHydrogenAtomDescription());
    }

    @Test
    public void getAcceptorReturnsConstructorValue() {
        HBondCondition cond = makeAnonymous();
        assertSame(acceptor, cond.getAcceptorAtomDescription());
    }

    @Test
    public void getAttachedReturnsConstructorValue() {
        HBondCondition cond = makeAnonymous();
        assertSame(attached, cond.getAttachedAtomDescription());
    }

    @Test
    public void containsIsTrueForDonorDescription() {
        HBondCondition cond = makeAnonymous();
        assertTrue(cond.contains(donor));
    }

    @Test
    public void containsIsTrueForHydrogenDescription() {
        HBondCondition cond = makeAnonymous();
        assertTrue(cond.contains(hydrogen));
    }

    @Test
    public void containsIsTrueForAcceptorDescription() {
        HBondCondition cond = makeAnonymous();
        assertTrue(cond.contains(acceptor));
    }

    @Test
    public void containsIsTrueForAttachedDescription() {
        HBondCondition cond = makeAnonymous();
        assertTrue(cond.contains(attached));
    }

    @Test
    public void containsIsFalseForUnrelatedDescription() {
        HBondCondition cond = makeAnonymous();
        assertFalse(cond.contains(new AtomDescription("CB")));
    }

    @Test
    public void toXmlContainsHBondConditionTag() {
        HBondCondition cond = makeAnonymous();
        String xml = cond.toXml();
        assertTrue(xml.contains("HBondCondition"));
        assertTrue(xml.contains("haMax"));
        assertTrue(xml.contains("dhaMin"));
        assertTrue(xml.contains("haaMin"));
    }

    @Test
    public void negateMarksConditionAsNegated() {
        HBondCondition cond = makeAnonymous();
        assertFalse(cond.isNegated());
        cond.negate();
        assertTrue(cond.isNegated());
    }

    @Test
    public void equalsTrueForSameInstance() {
        HBondCondition cond = makeAnonymous();
        assertTrue(cond.equals(cond));
    }

    @Test
    public void equalsFalseForNull() {
        HBondCondition cond = makeAnonymous();
        assertFalse(cond.equals(null));
    }
}
