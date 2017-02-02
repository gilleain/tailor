package tailor.engine;

import org.junit.Test;

import tailor.description.ChainDescription;
import tailor.description.ProteinDescription;
import tailor.measurement.DistanceMeasure;

public class MultiChain {
    
    @Test
    public void chainCentres() {
        String filename = "structures/1itk.pdb";
        
        ProteinDescription proteinDescription = new ProteinDescription();
        ChainDescription chainADescription = new ChainDescription();
        ChainDescription chainBDescription = new ChainDescription();
        proteinDescription.addChainDescription(chainADescription);
        proteinDescription.addChainDescription(chainBDescription);
        
        // TODO
        DistanceMeasure distanceMeasure = new DistanceMeasure("D", chainADescription, chainBDescription);
        
        proteinDescription.addMeasure(distanceMeasure);
        Run run = new Run(filename);
        run.addDescription(proteinDescription);
        
        Engine engine = EngineFactory.getEngine(proteinDescription);
        engine.run(run);
    }

}
