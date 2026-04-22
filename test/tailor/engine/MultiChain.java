package tailor.engine;

import org.junit.Test;

import tailor.description.ChainDescription;
import tailor.description.ProteinDescription;

public class MultiChain {
    
    @Test
    public void chainCentres() {
        String filename = "structures/1itk.pdb";
        
        ProteinDescription proteinDescription = new ProteinDescription("Test");
        ChainDescription chainADescription = new ChainDescription();
        ChainDescription chainBDescription = new ChainDescription();
        proteinDescription.addChainDescription(chainADescription);
        proteinDescription.addChainDescription(chainBDescription);
        
        // TODO
//        AtomDistanceMeasure distanceMeasure = new AtomDistanceMeasure("D", chainADescription, chainBDescription);
//        
//        proteinDescription.addMeasure(distanceMeasure);
//        Run run = new Run(filename);
//        run.addDescription(proteinDescription);
//        
        // TODO
//        Engine engine = EngineFactory.getEngine(proteinDescription);
//        engine.run(run);
    }

}
