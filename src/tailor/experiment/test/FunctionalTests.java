package tailor.experiment.test;

import static tailor.experiment.test.Helper.pathTo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import tailor.datasource.PDBReader;
import tailor.experiment.api.Operator;
import tailor.experiment.description.AtomDistanceDescription;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.plan.Planner;
import tailor.structure.Chain;
import tailor.structure.Structure;

public class FunctionalTests {
	
	private static final String DATA_DIR = "data";
	
	@Test
	public void helixTest() throws IOException {
		String filename = "helix.pdb";
		
		double angle = 180;
		double distance = 3.0;
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("O");
//		GroupDescription groupB = Helper.makeGroupDescription("C", "O");
		GroupDescription groupB = Helper.makeGroupDescription("N");
		chainDescription.addGroupDescriptions(groupA, groupB);
		chainDescription.addAtomListDescriptions(
				new AtomDistanceDescription(distance, pathTo(groupA, "O"), pathTo(groupB, "N"))
//				,
//				new AtomAngleDescription(angle, pathTo(groupA, "N"), pathTo(groupB, "O"), pathTo(groupB, "C"))
		);
		
		run(filename, chainDescription);
	}
	
	@Test
	public void hairpinTest() throws IOException {
		String filename = "hairpin.pdb";
		
		double distance = 5.0;
		
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N");
		GroupDescription groupB = Helper.makeGroupDescription("O");
		chainDescription.addGroupDescriptions(groupA, groupB);
		chainDescription.addAtomListDescriptions(
				new AtomDistanceDescription(distance, pathTo(groupA, "N"), pathTo(groupB, "O"))
		);
		
		run(filename, chainDescription);
	}
	
	private void run(String filename, ChainDescription chainDescription) throws IOException {
		List<Operator> pipeline = new Planner().plan(chainDescription);
		Helper.describe(pipeline);
		
		Structure structure = PDBReader.read(new File(DATA_DIR, filename));
		for (Structure chainStructure : structure.getSubstructures()) {
			Chain chain = (Chain) chainStructure;
			// TODO - do we have to reset?
			Helper.run(chain, pipeline);
		}
	}

}
