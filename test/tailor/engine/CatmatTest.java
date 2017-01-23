package tailor.engine;

import org.junit.Test;

import tailor.condition.DistanceBoundCondition;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.DescriptionFactory;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;
import tailor.description.Util;

public class CatmatTest {
    
    @Test
    public void catmatThree() {
        String filename = "structures/2bop.pdb";
        
        DescriptionFactory factory = new DescriptionFactory();
        factory.addResidues(4);
            
        Description description = factory.getProduct();
        ChainDescription waterChain = new ChainDescription("W");
        description.addSubDescription(waterChain);
        
        GroupDescription water = new GroupDescription("HOH");
        waterChain.addGroupDescription(water);
        AtomDescription waterOxygen = new AtomDescription("O");
        water.addAtomDescription(waterOxygen);
        
        Util.labelTree(description);
//        Util.printHierarchy(description);
        
        ChainDescription proteinChain = (ChainDescription)description.getSubDescriptionAt(0);
        Description carbonylOxygenI  = proteinChain.getPath(0, "O");
        Description carbonylOxygenI2 = proteinChain.getPath(2, "O");
        Description waterPath = waterChain.getPathByGroupName("HOH", "O");
        
        System.out.println("COI " + carbonylOxygenI + 
                           " COIP2 " + carbonylOxygenI2 + 
                           " W " + waterPath);
        
        description.addCondition(
                new DistanceBoundCondition(
                       "i.O-W", carbonylOxygenI, waterPath,  3.5, 1));
        description.addCondition(
                new DistanceBoundCondition(
                    "i+2.O-W", carbonylOxygenI2, waterPath, 3.5, 1));
        
        description.addMeasure(factory.createPhiMeasure("phi2", 2));
        description.addMeasure(factory.createPhiMeasure("psi2", 2));
        Run run = new Run(filename);
        run.addDescription((ProteinDescription)description);
        
        Engine engine = EngineFactory.getEngine(description);
        engine.run(run);
    }
}
