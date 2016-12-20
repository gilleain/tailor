package tailor.match;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;
import tailor.structure.Chain;
import tailor.structure.StructureFactory;

public class TestGroupMatcher {
    
    @Test
    public void initialStart() {
        ChainDescription chainDescription = new ChainDescription();
        chainDescription.addGroupDescription(makeGroupDescription("GLY", "CA"));
        chainDescription.addGroupDescription(makeGroupDescription("ALA", "CA"));
        
        Chain chain = StructureFactory.makeChain("GAA");
        GroupMatcher groupMatcher = new GroupMatcher();
        List<Match> matches = groupMatcher.match(chainDescription, chain);
        assertEquals(1, matches.size());
    }
    
    @Test
    public void nonInitialStart() {
        ChainDescription chainDescription = new ChainDescription();
        chainDescription.addGroupDescription(makeGroupDescription("GLY", "CA"));
        chainDescription.addGroupDescription(makeGroupDescription("ALA", "CA"));
        
        Chain chain = StructureFactory.makeChain("GGA");
        GroupMatcher groupMatcher = new GroupMatcher();
        List<Match> matches = groupMatcher.match(chainDescription, chain);
        assertEquals(1, matches.size());
    }
    
    @Test
    public void nonOverlapping() {
        ChainDescription chainDescription = new ChainDescription();
        chainDescription.addGroupDescription(makeGroupDescription("GLY", "CA"));
        chainDescription.addGroupDescription(makeGroupDescription("ALA", "CA"));
        
        Chain chain = StructureFactory.makeChain("GAGA");
        GroupMatcher groupMatcher = new GroupMatcher();
        List<Match> matches = groupMatcher.match(chainDescription, chain);
        assertEquals(2, matches.size());
    }
    
    @Test
    public void overlapping() {
        ChainDescription chainDescription = new ChainDescription();
        chainDescription.addGroupDescription(makeGroupDescription("GLY", "CA"));
        chainDescription.addGroupDescription(makeGroupDescription("GLY", "CA"));
        
        Chain chain = StructureFactory.makeChain("GGG");
        GroupMatcher groupMatcher = new GroupMatcher();
        List<Match> matches = groupMatcher.match(chainDescription, chain);
        assertEquals(2, matches.size());
    }
    
    private GroupDescription makeGroupDescription(String residueName, String... atomNames) {
        GroupDescription groupDescription = new GroupDescription(residueName);
        for (String atomName : atomNames) {
            groupDescription.addAtomDescription(new AtomDescription(atomName));
        }
        return groupDescription;
    }

}
