package tailor.engine;

import java.io.IOException;

import org.junit.Test;

import tailor.description.ChainDescription;
import tailor.description.DescriptionFactory;
import tailor.description.DescriptionFactory.MeasureBuilder;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;
import tailor.description.atom.AtomDistanceRangeDescription;
import tailor.description.group.GroupSequenceDescription;
import tailor.engine.EngineFactory.EngineType;

public class SingleChainTest {
    
    
    @Test
    public void simpleHBondTest() throws IOException {
        String filename = "structures/2bop.pdb";
        
        DescriptionFactory factory = new DescriptionFactory("A");
        factory.addResidues(3);
        ChainDescription chainDescription = factory.getChainDescription("A");
        GroupDescription group0 = chainDescription.getGroupDescriptions().get(0);
        GroupDescription group1 = chainDescription.getGroupDescriptions().get(1);
        GroupDescription group2 = chainDescription.getGroupDescriptions().get(2);
        chainDescription.addGroupSequenceDescriptions(new GroupSequenceDescription(group0, group1, 1));
        chainDescription.addGroupSequenceDescriptions(new GroupSequenceDescription(group1, group2, 1));
        DescriptionPath carbonylOxygen = DescriptionPath.getPath(chainDescription, 0, "N");
        DescriptionPath amineNitrogen = DescriptionPath.getPath(chainDescription, 1, "O");
        
        chainDescription.addAtomListDescriptions(
                new AtomDistanceRangeDescription("i.O->(i+3).N", 2.5, 4.5, carbonylOxygen, amineNitrogen)
        );
        
        MeasureBuilder measureBuilder = factory.measures();
        chainDescription.addAtomListMeasures(
        	measureBuilder.createPhiMeasure("A", 2, "psi2"), 
        	measureBuilder.createPsiMeasure("A", 2, "phi2")
        );
        
        Run run = new Run(chainDescription, filename);
        Engine engine = EngineFactory.getEngine(run, new SysoutResultsPrinter(), EngineType.PLAN);
        engine.run();
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
