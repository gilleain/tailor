package tailor.condition;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import tailor.description.Description;
import tailor.match.Match;

/**
 * Tests for the logical combinator conditions: All, Any, None, OnlyOne.
 */
public class TestLogicalConditions {

    // --- Stub factory methods ---

    private static Condition alwaysTrue() {
        return new Condition() {
            public boolean satisfiedBy(Match match) { return true; }
            public boolean contains(Description d) { return false; }
            public String toXml() { return ""; }
            public void negate() {}
            public boolean isNegated() { return false; }
            public boolean hasProperty(String key) { return false; }
            public Object getProperty(String key) { return null; }
        };
    }

    private static Condition alwaysFalse() {
        return new Condition() {
            public boolean satisfiedBy(Match match) { return false; }
            public boolean contains(Description d) { return false; }
            public String toXml() { return ""; }
            public void negate() {}
            public boolean isNegated() { return false; }
            public boolean hasProperty(String key) { return false; }
            public Object getProperty(String key) { return null; }
        };
    }

    private static ArrayList<Condition> list(Condition... conditions) {
        return new ArrayList<>(Arrays.asList(conditions));
    }

    // --- All ---

    @Test
    public void allWithEmptyListIsTrue() {
        All all = new All();
        assertTrue(all.satisfiedBy(null));
    }

    @Test
    public void allWithNullListIsTrue() {
        All all = new All((List<Condition>) null);
        assertTrue(all.satisfiedBy(null));
    }

    @Test
    public void allConditionsTrueIsTrue() {
        All all = new All(Arrays.asList(alwaysTrue(), alwaysTrue(), alwaysTrue()));
        assertTrue(all.satisfiedBy(null));
    }

    @Test
    public void allWithOneFalseConditionIsFalse() {
        All all = new All(Arrays.asList(alwaysTrue(), alwaysFalse(), alwaysTrue()));
        assertFalse(all.satisfiedBy(null));
    }

    @Test
    public void allConditionsFalseIsFalse() {
        All all = new All(Arrays.asList(alwaysFalse(), alwaysFalse()));
        assertFalse(all.satisfiedBy(null));
    }

    @Test
    public void allToStringContainsAllKeyword() {
        All all = new All();
        assertTrue(all.toString().startsWith("All"));
    }

    // --- Any ---

    @Test
    public void anyWithEmptyListIsFalse() {
        Any any = new Any(new ArrayList<>());
        assertFalse(any.satisfiedBy(null));
    }

    @Test
    public void anyWithAllFalseIsFalse() {
        Any any = new Any(list(alwaysFalse(), alwaysFalse()));
        assertFalse(any.satisfiedBy(null));
    }

    @Test
    public void anyWithOneTrueIsTrue() {
        Any any = new Any(list(alwaysFalse(), alwaysTrue(), alwaysFalse()));
        assertTrue(any.satisfiedBy(null));
    }

    @Test
    public void anyWithAllTrueIsTrue() {
        Any any = new Any(list(alwaysTrue(), alwaysTrue()));
        assertTrue(any.satisfiedBy(null));
    }

    @Test
    public void anyToStringContainsAnyKeyword() {
        Any any = new Any(new ArrayList<>());
        assertTrue(any.toString().startsWith("Any"));
    }

    // --- None ---

    @Test
    public void noneWithEmptyListIsTrue() {
        None none = new None(new ArrayList<>());
        assertTrue(none.satisfiedBy(null));
    }

    @Test
    public void noneWithAllFalseIsTrue() {
        None none = new None(list(alwaysFalse(), alwaysFalse()));
        assertTrue(none.satisfiedBy(null));
    }

    @Test
    public void noneWithOneTrueIsFalse() {
        None none = new None(list(alwaysFalse(), alwaysTrue()));
        assertFalse(none.satisfiedBy(null));
    }

    @Test
    public void noneWithAllTrueIsFalse() {
        None none = new None(list(alwaysTrue(), alwaysTrue()));
        assertFalse(none.satisfiedBy(null));
    }

    @Test
    public void noneToStringContainsNoneKeyword() {
        None none = new None(new ArrayList<>());
        assertTrue(none.toString().startsWith("None"));
    }

    // --- OnlyOne ---

    @Test
    public void onlyOneWithEmptyListIsFalse() {
        OnlyOne onlyOne = new OnlyOne(new ArrayList<>());
        assertFalse(onlyOne.satisfiedBy(null));
    }

    @Test
    public void onlyOneWithNoneSatisfiedIsFalse() {
        OnlyOne onlyOne = new OnlyOne(list(alwaysFalse(), alwaysFalse()));
        assertFalse(onlyOne.satisfiedBy(null));
    }

    @Test
    public void onlyOneWithExactlyOneSatisfiedIsTrue() {
        OnlyOne onlyOne = new OnlyOne(list(alwaysFalse(), alwaysTrue(), alwaysFalse()));
        assertTrue(onlyOne.satisfiedBy(null));
    }

    @Test
    public void onlyOneWithTwoSatisfiedIsFalse() {
        OnlyOne onlyOne = new OnlyOne(list(alwaysTrue(), alwaysTrue(), alwaysFalse()));
        assertFalse(onlyOne.satisfiedBy(null));
    }

    @Test
    public void onlyOneWithAllSatisfiedIsFalse() {
        OnlyOne onlyOne = new OnlyOne(list(alwaysTrue(), alwaysTrue(), alwaysTrue()));
        assertFalse(onlyOne.satisfiedBy(null));
    }

    @Test
    public void onlyOneToStringContainsOnlyOneKeyword() {
        OnlyOne onlyOne = new OnlyOne(new ArrayList<>());
        assertTrue(onlyOne.toString().startsWith("OnlyOne"));
    }
}
