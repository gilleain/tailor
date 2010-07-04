package tailor.engine;

import org.junit.Test;

import tailor.Level;
import tailor.datasource.Structure;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;

public class RangedChainSingleEngineTest {
    
    @Test
    public void simpleTest() {
        // make the data
        Structure protein = new Structure(Level.PROTEIN);
        Structure chain = new Structure(Level.CHAIN);
        protein.addSubStructure(chain);
        for (int i = 0; i < 10; i++) {
            Structure residue = new Structure(Level.RESIDUE);
            chain.addSubStructure(residue);
        }
        
        // make the pattern
        ProteinDescription proteinDescription = new ProteinDescription();
        ChainDescription chainDescription = new ChainDescription();
        GroupDescription groupDescriptionA = new GroupDescription();
        GroupDescription groupDescriptionB = new GroupDescription();
        chainDescription.addGroupDescription(groupDescriptionA);
        chainDescription.addGroupDescription(groupDescriptionB);
        proteinDescription.addChainDescription(chainDescription);
        
        // match
        RangedSingleChainEngine engine = new RangedSingleChainEngine();
        engine.match(proteinDescription, protein);
    }

}
