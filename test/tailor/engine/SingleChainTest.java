package tailor.engine;

import java.io.IOException;

import org.junit.Test;

import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.DescriptionFactory;
import tailor.description.DescriptionFactory.MeasureBuilder;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;
import tailor.description.atom.AtomDistanceRangeDescription;
import tailor.description.atom.HBondDescription;
import tailor.engine.EngineFactory.EngineType;

public class SingleChainTest {
	

    @Test
    public void catmatThree() throws IOException { // TODO - actually multi-chain!
        String filename = "structures/2bop.pdb";
        
        DescriptionFactory factory = new DescriptionFactory("A");
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
        
        MeasureBuilder measureBuilder = factory.measures();
        chainDescription.addAtomListMeasures(
        	measureBuilder.createPhiMeasure("A", 2, "phi2"), 
        	measureBuilder.createPsiMeasure("A", 2, "psi2")
        );
        
        Run run = new Run(chainDescription, filename);
        Engine engine = EngineFactory.getEngine(run, new SysoutResultsPrinter(), EngineType.PLAN);
        engine.run();
    }
    
    
    @Test
    public void simpleHBondTest() throws IOException {
        String filename = "structures/2bop.pdb";
        
        DescriptionFactory factory = new DescriptionFactory("A");
        factory.addResiduesAsSegment(3);
        ChainDescription chainDescription = factory.getChainDescription("A");

        DescriptionPath carbonylOxygen = DescriptionPath.getPathByNumber(chainDescription, 0, "N");
        DescriptionPath amineNitrogen = DescriptionPath.getPathByNumber(chainDescription, 1, "O");
        
        chainDescription.addAtomListDescriptions(
                new AtomDistanceRangeDescription("i.O->(i+3).N", 2.5, 4.5, carbonylOxygen, amineNitrogen)
        );
        
        MeasureBuilder measureBuilder = factory.measures();
        chainDescription.addAtomListMeasures(
        	measureBuilder.createPhiMeasure("A", 2, "phi2"), 
        	measureBuilder.createPsiMeasure("A", 2, "psi2")
        );
        
        Run run = new Run(chainDescription, filename);
        Engine engine = EngineFactory.getEngine(run, new SysoutResultsPrinter(), EngineType.PLAN);
        engine.run();
    }
    
    @Test
    public void geometricHBondTest() throws IOException {
        String filename = "structures/1a2p.pdb";
        
        String chainLabel = "A";
        DescriptionFactory factory = new DescriptionFactory(chainLabel);
        factory.setAddBackboneAmineHydrogens(true);
        factory.addResiduesAsSegment(5);
        // i.O->(i+4).N
        HBondDescription hBond = factory.listDescriptions().createHBondDescription(chainLabel, 3.5, 110, 180, 4, 0, "HBond");
        
        ChainDescription chainDescription = factory.getChainDescription("A");
        chainDescription.addAtomListDescriptions(hBond);
        MeasureBuilder measureBuilder = factory.measures();
        chainDescription.addAtomListMeasures(
        	measureBuilder.createPhiMeasure("A", 2, "phi2"), 
        	measureBuilder.createPsiMeasure("A", 2, "psi2"),
        	hBond.createMeasure()
        );
        
        Run run = new Run(chainDescription, filename);
        Engine engine = EngineFactory.getEngine(run, new SysoutResultsPrinter(), EngineType.PLAN);
        engine.run();
    }

}
