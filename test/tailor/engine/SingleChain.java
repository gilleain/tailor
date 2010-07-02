package tailor.engine;

import org.junit.Test;

import tailor.condition.DistanceBoundCondition;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.DescriptionFactory;
import tailor.description.ProteinDescription;

public class SingleChain {
    
    @Test
    public void simpleTest() {
        String filename = "structures/2bop.pdb";
        
        DescriptionFactory factory = new DescriptionFactory();
        factory.addResidues(4);
        ChainDescription carbonylOxygen = 
            factory.getChainDescription("A").getPath(0, "O");
        ChainDescription amineNitrogen = 
            factory.getChainDescription("A").getPath(3, "N");
        factory.getChainDescription("A").addCondition(
                new DistanceBoundCondition(
                        "i.O->(i+3).N", carbonylOxygen, amineNitrogen, 3.5, 1));
        
        Run run = new Run(filename);
        run.addMeasure(factory.createPhiMeasure("psi2", 2));
        run.addMeasure(factory.createPsiMeasure("phi2", 2));
        
        Description description = factory.getProduct(); 
        run.addDescription((ProteinDescription)description);
        
        Engine engine = EngineFactory.getEngine(description);
        engine.run(run);
    }

}
