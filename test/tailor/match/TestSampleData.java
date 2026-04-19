package tailor.match;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import tailor.condition.atom.AtomTorsionRangeCondition;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.experiment.description.DescriptionPath;
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
        chainDescription.addGroupDescription(makeGroupDescription("i - 1", "N", "CA", "C", "O"));
        chainDescription.addGroupDescription(makeGroupDescription("i",     "N", "CA", "C", "O"));
        chainDescription.addGroupDescription(makeGroupDescription("i + 1", "N", "CA", "C", "O"));
        
        DescriptionPath cIM1 = chainDescription.getDescriptionPathByGroupLabel("i - 1", "C");
        DescriptionPath nI   = chainDescription.getDescriptionPathByGroupLabel("i",     "N");
        DescriptionPath caI  = chainDescription.getDescriptionPathByGroupLabel("i",     "CA");
        DescriptionPath cI   = chainDescription.getDescriptionPathByGroupLabel("i",     "C");
        DescriptionPath nI1  = chainDescription.getDescriptionPathByGroupLabel("i + 1", "N");
        
        chainDescription.addCondition(new AtomTorsionRangeCondition("phi", 60, 120, cIM1, nI, caI, cI));
        chainDescription.addCondition(new AtomTorsionRangeCondition("psi", 60, 120, nI, caI, cI, nI1));
        
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
