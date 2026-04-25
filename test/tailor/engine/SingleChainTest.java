package tailor.engine;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.DescriptionFactory;
import tailor.description.DescriptionFactory.MeasureBuilder;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;
import tailor.description.atom.AtomDistanceRangeDescription;

public class SingleChainTest {
    
    
    @Test
    public void simpleHBondTest() {
        String filename = "structures/2bop.pdb";
        
        DescriptionFactory factory = new DescriptionFactory("A");
        factory.addResidues(4);
        ChainDescription chainDescription = factory.getChainDescription("A");
        GroupDescription groupA = new GroupDescription();
        GroupDescription groupB = new GroupDescription();
        
        DescriptionPath carbonylOxygen = new DescriptionPath(groupA, new AtomDescription("O"));
        DescriptionPath amineNitrogen = new DescriptionPath(groupB, new AtomDescription("O"));
        
        chainDescription.addAtomListDescriptions(
                new AtomDistanceRangeDescription("i.O->(i+3).N", 2.5, 4.5, carbonylOxygen, amineNitrogen)
        );
        
        MeasureBuilder measureBuilder = factory.measures();
        chainDescription.addAtomListMeasures(
        	measureBuilder.createPhiMeasure("A", 2, "psi2"), 
        	measureBuilder.createPsiMeasure("A", 2, "phi2")
        );
        
        Run run = new Run(filename);
//        run.addDescription((ProteinDescription)chainDescription);
        
        // TODO
//        Engine engine = EngineFactory.getEngine(description);
//        engine.run(run);
    }
    
    @Test
    public void geometricHBondTest() {
        String filename = "structures/1a2p.pdb";
        
        String chainLabel = "A";
        DescriptionFactory factory = new DescriptionFactory(chainLabel);
        factory.setAddBackboneAmineHydrogens(true);
        factory.addResidues(5);
        // i.O->(i+4).N
        factory.listDescriptions().createHBondDescription(chainLabel, 3.5, 90, 90, 4, 0, "HBond");
        
        ChainDescription description = factory.getChainDescription("A");
        MeasureBuilder measureBuilder = factory.measures();
        description.addAtomListMeasures(
        	measureBuilder.createPhiMeasure("A", 2, "phi2"), 
        	measureBuilder.createPsiMeasure("A", 2, "psi2")
        );
        
        Run run = new Run(filename);
        
        // TODO
//        run.addDescription((ProteinDescription)description);
//        Engine engine = EngineFactory.getEngine(description);
//        engine.run(run);
    }


}
