package tailor.match;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import tailor.condition.TorsionBoundCondition;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.source.PDBReader;
import tailor.structure.Chain;
import tailor.structure.Protein;
import tailor.structure.Structure;

public class TestSampleData {
    
    private static final String DIR = "data";
    
    @Test
    public void helixBaseTest() throws IOException {
        ChainDescription chainDescription = new ChainDescription();
        chainDescription.addGroupDescription(makeGroupDescription("a", "N", "CA", "C", "O"));
        chainDescription.addGroupDescription(makeGroupDescription("b", "N", "CA", "C", "O"));
        chainDescription.addGroupDescription(makeGroupDescription("c", "N", "CA", "C", "O"));
        
        Protein structure = PDBReader.read(new File(DIR, "helix.pdb"));
        Chain chain = structure.getChains().get(0);
        
        assertEquals(16, chain.getGroups().size());
        
        GroupMatcher groupMatcher = new GroupMatcher();
        List<Match> matches = groupMatcher.match(chainDescription, chain);
        assertEquals(14, matches.size());
        
        printMatches(matches);
    }
    
    @Test
    public void helixTorsionTest() throws IOException {
        ChainDescription chainDescription = new ChainDescription();
        chainDescription.addGroupDescription(makeGroupDescription("i",     "N", "CA", "C", "O"));
        chainDescription.addGroupDescription(makeGroupDescription("i + 1", "N", "CA", "C", "O"));
        chainDescription.addGroupDescription(makeGroupDescription("i + 2", "N", "CA", "C", "O"));
        
        Description caI  = chainDescription.getPathByGroupLabel("i", "CA");
        Description cI   = chainDescription.getPathByGroupLabel("i", "C");
        Description nI1  = chainDescription.getPathByGroupLabel("i + 1", "N");
        Description caI1 = chainDescription.getPathByGroupLabel("i + 1", "CA");
        Description cI1 = chainDescription.getPathByGroupLabel("i + 1", "C");
        
        chainDescription.addCondition(new TorsionBoundCondition("phi", caI, cI, nI1, caI1, 90, 30));
        chainDescription.addCondition(new TorsionBoundCondition("psi", cI, nI1, caI1, cI1, 90, 30));
        
        Protein structure = PDBReader.read(new File(DIR, "helix.pdb"));
        Chain chain = structure.getChains().get(0);
        
        GroupMatcher groupMatcher = new GroupMatcher();
        List<Match> matches = groupMatcher.match(chainDescription, chain);
        printMatches(matches);
    }
    
    private void printMatches(List<Match> matches) {
        int counter = 1;
        for (Match match : matches) {
            System.out.println("Match " + counter + " " + matchToString(match));
            counter++;
        } 
    }
    
    private String matchToString(Match match) {
        final StringBuilder result = new StringBuilder();
        match.accept(new MatchVisitor() {

            @Override
            public boolean visitMatch(Match match) {
                return true;
            }

            @Override
            public boolean visitDescription(Description description) {
                return true;
            }

            @Override
            public boolean visitStructure(Structure structure) {
                result.append(structure.getName()).append(",");
                return true;
            }
            
        });
        return result.toString();
    }
    
    private GroupDescription makeGroupDescription(String label, String... atomNames) {
        GroupDescription groupDescription = new GroupDescription();
        for (String atomName : atomNames) {
            groupDescription.addAtomDescription(new AtomDescription(atomName));
        }
        return groupDescription;
    }

}
