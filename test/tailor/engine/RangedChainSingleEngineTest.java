package tailor.engine;

import java.util.List;

import org.junit.Test;

import tailor.datasource.Structure;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;
import tailor.description.RangedGroupDescription;

public class RangedChainSingleEngineTest {
    
    @Test
    public void unrangedTest() {
        // make the data
        Structure protein = StructureFactory.makeChain("GTYPEDFASED");
        
        // make the pattern
        ChainDescription chainDescription = new ChainDescription();
        GroupDescription groupDescriptionA = new GroupDescription();
        GroupDescription groupDescriptionB = new GroupDescription();
        GroupDescription groupDescriptionC = new GroupDescription();
        chainDescription.addGroupDescription(groupDescriptionA);
        chainDescription.addGroupDescription(groupDescriptionB);
        chainDescription.addGroupDescription(groupDescriptionC);
        
        // match
        RangedSingleChainEngine engine = new RangedSingleChainEngine();
        List<Match> matches = engine.match(chainDescription, protein);
        for (Match match : matches) {
            System.out.println(match);
        }
    }
    
    @Test
    public void rangedTest() {
     // make the data
        Structure protein = StructureFactory.makeChain("GTYPEDFASED");
        
        // make the pattern
        ChainDescription chainDescription = new ChainDescription();
        RangedGroupDescription groupDescription = new RangedGroupDescription();
        chainDescription.addGroupDescription(groupDescription);
        
        // match
        RangedSingleChainEngine engine = new RangedSingleChainEngine();
        List<Match> matches = engine.match(chainDescription, protein);
        for (Match match : matches) {
            System.out.println(match);
        }
    }

}
