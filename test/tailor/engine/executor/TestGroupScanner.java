package tailor.engine.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import tailor.engine.execute.GroupScanner;
import tailor.match.Match;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Structure;

public class TestGroupScanner {
    
    @Test
    public void twoFromTwo() {
        Chain chain = makeChain(2);
        GroupScanner scanner = new GroupScanner(2);
        List<Match> matches = scanner.scan(chain);
        assertEquals("Number of matches", 1, matches.size());
        
        assertThat(matches.get(0), 
                matchType(Chain.class, 
                        matchType(Group.class), matchType(Group.class)
                )
       );
    }
    
    @Test
    public void twoFromThree() {
        Chain chain = makeChain(3);
        GroupScanner scanner = new GroupScanner(2);
        List<Match> matches = scanner.scan(chain);
        assertEquals("Number of matches", 2, matches.size());
        assertThat(matches.get(0), 
                matchType(Chain.class, 
                        matchType(Group.class), matchType(Group.class)
                )
       );
    }
    
    @SafeVarargs
    private final Matcher<Match> matchType(
            final Class<? extends Structure> type, Matcher<Match>... subTypes) {
        return new BaseMatcher<Match>() {
            
            @Override
            public boolean matches(Object item) {
                Match match = (Match) item;
                if (match.getStructure().getClass() == type) {
                    int index = 0;
                    for (Matcher<Match> subType : subTypes) {
                        if (!subType.matches(match.getMatch(index))) {
                            return false;
                        }
                        index++;
                    }
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Match type").appendValue(type);
            }
            
            @Override
            public void describeMismatch(final Object item, final Description description) {
                Match match = (Match) item;
                description.appendText("was").appendValue(match.getStructure().getClass());
            }
            
        };
    }

    private Chain makeChain(int numberOfResidues) {
        Chain chain = new Chain();
        
        for (int index = 0; index < numberOfResidues; index++) {
            chain.addGroup(new Group());
        }
        
        return chain;
    }

}
