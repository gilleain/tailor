package tailor.engine;

import org.junit.Test;

import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.DescriptionFactory;
import tailor.description.ProteinDescription;
import tailor.experiment.condition.atom.AtomDistanceRangeCondition;
import tailor.experiment.description.AtomDescription;
import tailor.experiment.description.DescriptionPath;
import tailor.experiment.description.GroupDescription;

public class SingleChainTest {
    
    
    @Test
    public void simpleHBondTest() {
        String filename = "structures/2bop.pdb";
        
        DescriptionFactory factory = new DescriptionFactory();
        factory.addResidues(4);
        ChainDescription chainDescription = new ChainDescription("A");
        GroupDescription groupA = new GroupDescription();
        GroupDescription groupB = new GroupDescription();
        
        DescriptionPath carbonylOxygen = new DescriptionPath(groupA, new AtomDescription("O"));
        DescriptionPath amineNitrogen = new DescriptionPath(groupB, new AtomDescription("O"));
        
        chainDescription.addCondition(
                new AtomDistanceRangeCondition("i.O->(i+3).N", 2.5, 4.5, carbonylOxygen, amineNitrogen));
        
        chainDescription.addMeasure(factory.createPhiMeasure("psi2", 2));
        chainDescription.addMeasure(factory.createPsiMeasure("phi2", 2));
        
        Run run = new Run(filename);
//        run.addDescription((ProteinDescription)chainDescription);
        
        // TODO
//        Engine engine = EngineFactory.getEngine(description);
//        engine.run(run);
    }
    
    @Test
    public void geometricHBondTest() {
        String filename = "structures/1a2p.pdb";
        
        DescriptionFactory factory = new DescriptionFactory();
        factory.setAddBackboneAmineHydrogens(true);
        factory.addResidues(5);
        // i.O->(i+4).N
        factory.createHBondCondition(3.5, 90, 90, 4, 0);
        
        Description description = factory.getProduct();
        description.addMeasure(factory.createPhiMeasure("psi2", 2));
        description.addMeasure(factory.createPsiMeasure("phi2", 2));
        
        Run run = new Run(filename);
        run.addDescription((ProteinDescription)description);
        
        // TODO
//        Engine engine = EngineFactory.getEngine(description);
//        engine.run(run);
    }


}
