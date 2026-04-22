package tailor.engine;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.DescriptionFactory;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;
import tailor.description.atom.AtomDistanceRangeDescription;

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
        
        chainDescription.addAtomListDescriptions(
                new AtomDistanceRangeDescription("i.O->(i+3).N", 2.5, 4.5, carbonylOxygen, amineNitrogen)
        );
        
        chainDescription.addAtomListMeasures(
        		factory.createPhiMeasure("psi2", 2), factory.createPsiMeasure("phi2", 2)
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
        
        DescriptionFactory factory = new DescriptionFactory();
        factory.setAddBackboneAmineHydrogens(true);
        factory.addResidues(5);
        // i.O->(i+4).N
        factory.createHBondCondition(3.5, 90, 90, 4, 0);
        
        ChainDescription description = new ChainDescription("A");
        description.addAtomListMeasures(
        		factory.createPhiMeasure("psi2", 2), factory.createPsiMeasure("phi2", 2)
        );
        
        Run run = new Run(filename);
        
        // TODO
//        run.addDescription((ProteinDescription)description);
//        Engine engine = EngineFactory.getEngine(description);
//        engine.run(run);
    }


}
