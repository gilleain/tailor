package tailor.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestPropertyCondition {

    @Test
    public void keyEqualsReturnsTrueForMatchingKey() {
        PropertyCondition cond = new PropertyCondition("Name", "CA");
        assertTrue(cond.keyEquals("Name"));
    }

    @Test
    public void keyEqualsReturnsFalseForDifferentKey() {
        PropertyCondition cond = new PropertyCondition("Name", "CA");
        assertFalse(cond.keyEquals("Type"));
    }

    @Test
    public void valueEqualsReturnsTrueForMatchingValue() {
        PropertyCondition cond = new PropertyCondition("Name", "CA");
        assertTrue(cond.valueEquals("CA"));
    }

    @Test
    public void valueEqualsReturnsFalseForDifferentValue() {
        PropertyCondition cond = new PropertyCondition("Name", "CA");
        assertFalse(cond.valueEquals("N"));
    }

    @Test
    public void getValueReturnsConstructorValue() {
        PropertyCondition cond = new PropertyCondition("Name", "GLY");
        assertEquals("GLY", cond.getValue());
    }

    @Test
    public void equalsTrueForSameKeyAndValue() {
        PropertyCondition a = new PropertyCondition("Name", "CA");
        PropertyCondition b = new PropertyCondition("Name", "CA");
        assertTrue(a.equals(b));
    }

    @Test
    public void equalsFalseForDifferentValue() {
        PropertyCondition a = new PropertyCondition("Name", "CA");
        PropertyCondition b = new PropertyCondition("Name", "N");
        assertFalse(a.equals(b));
    }

    @Test
    public void equalsFalseForDifferentKey() {
        PropertyCondition a = new PropertyCondition("Name", "CA");
        PropertyCondition b = new PropertyCondition("Type", "CA");
        assertFalse(a.equals(b));
    }

    @Test
    public void equalsTrueForSameInstance() {
        PropertyCondition a = new PropertyCondition("Name", "CA");
        assertTrue(a.equals(a));
    }

    @Test
    public void equalsFalseForNull() {
        PropertyCondition a = new PropertyCondition("Name", "CA");
        assertFalse(a.equals(null));
    }

    @Test
    public void equalsFalseForOtherType() {
        PropertyCondition a = new PropertyCondition("Name", "CA");
        assertFalse(a.equals("Name : CA"));
    }

    @Test
    public void satisfiedByAlwaysReturnsFalse() {
        PropertyCondition cond = new PropertyCondition("Name", "CA");
        // satisfiedBy is not yet implemented (returns false unconditionally)
        assertFalse(cond.satisfiedBy(null));
    }

    @Test
    public void toStringFormatsAsKeyColonValue() {
        PropertyCondition cond = new PropertyCondition("Name", "CA");
        assertEquals("Name : CA", cond.toString());
    }

    @Test
    public void negateMarksConditionAsNegated() {
        PropertyCondition cond = new PropertyCondition("Name", "CA");
        assertFalse(cond.isNegated());
        cond.negate();
        assertTrue(cond.isNegated());
    }

    @Test
    public void hasPropertyReturnsFalse() {
        PropertyCondition cond = new PropertyCondition("Name", "CA");
        assertFalse(cond.hasProperty("Name"));
    }

    @Test
    public void getPropertyReturnsNull() {
        PropertyCondition cond = new PropertyCondition("Name", "CA");
        assertEquals(null, cond.getProperty("Name"));
    }
}
