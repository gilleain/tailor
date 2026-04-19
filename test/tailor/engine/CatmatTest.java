package tailor.engine;


import org.junit.Test;

import tailor.experiment.description.AtomDescription;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.description.atom.AtomDistanceRangeDescription;

public class CatmatTest {
    
    @Test
    public void catmatThree() {
        String filename = "structures/2bop.pdb";
        
//        DescriptionFactory factory = new DescriptionFactory();
//        factory.addResidues(4);
//            
//        Description description = factory.getProduct();
        ChainDescription waterChain = new ChainDescription("W");
//        description.addSubDescription(waterChain);
        
        GroupDescription water = new GroupDescription("HOH");
        waterChain.addGroupDescription(water);
        AtomDescription waterOxygen = new AtomDescription("O");
        water.addAtomDescription(waterOxygen);
        
//        Util.labelTree(description);
//        Util.printHierarchy(description);
        
//        ChainDescription proteinChain = (ChainDescription)description.getSubDescriptionAt(0);
        ChainDescription chainDescription = new ChainDescription(); 
        GroupDescription groupDescription1 = new GroupDescription();
        GroupDescription groupDescription2 = new GroupDescription();
        DescriptionPath carbonylOxygenI  = new DescriptionPath(groupDescription1, new AtomDescription("O"));
        DescriptionPath carbonylOxygenI2 = new DescriptionPath(groupDescription2, new AtomDescription("O"));
        DescriptionPath waterPath = new DescriptionPath(water, waterOxygen);
        
        System.out.println("COI " + carbonylOxygenI + 
                           " COIP2 " + carbonylOxygenI2 + 
                           " W " + waterPath);
        
        chainDescription.addAtomListDescriptions(
        		new AtomDistanceRangeDescription("i.O-W", 2.5, 4.5, carbonylOxygenI, waterPath),
        		new AtomDistanceRangeDescription("i+2.O-W", 2.5, 4.5, carbonylOxygenI2, waterPath)
        );
        
        // TODO
//        chainDescription.addMeasure(factory.createPhiMeasure("phi2", 2));
//        chainDescription.addMeasure(factory.createPhiMeasure("psi2", 2));
//        Run run = new Run(filename);
//        run.addDescription((ProteinDescription)description);
        
        // TODO
//        Engine engine = EngineFactory.getEngine(description);
//        engine.run(run);
    }
}
