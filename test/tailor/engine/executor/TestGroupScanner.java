package tailor.engine.executor;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import tailor.engine.execute.GroupScanner;
import tailor.match.Match;
import tailor.structure.Chain;
import tailor.structure.Group;

public class TestGroupScanner {
    
    @Test
    public void twoFromTwo() {
        Chain chain = makeChain(2);
        GroupScanner scanner = new GroupScanner(2);
        List<Match> matches = scanner.scan(chain);
        assertEquals("Number of matches", 1, matches.size());
    }
    
    @Test
    public void twoFromThree() {
        Chain chain = makeChain(3);
        GroupScanner scanner = new GroupScanner(2);
        List<Match> matches = scanner.scan(chain);
        assertEquals("Number of matches", 2, matches.size());
    }

    private Chain makeChain(int numberOfResidues) {
        Chain chain = new Chain();
        
        for (int index = 0; index < numberOfResidues; index++) {
            chain.addGroup(new Group());
        }
        
        return chain;
    }

}
