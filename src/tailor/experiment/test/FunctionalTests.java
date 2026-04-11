package tailor.experiment.test;

import static tailor.experiment.test.Helper.pathTo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import tailor.datasource.PDBReader;
import tailor.experiment.api.Operator;
import tailor.experiment.description.AtomAngleRangeDescription;
import tailor.experiment.description.AtomDistanceRangeDescription;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.plan.Planner;
import tailor.structure.Chain;
import tailor.structure.Structure;

public class FunctionalTests {
	
	private static final String DATA_DIR = "data";
	
	private ChainDescription onHBond = makeON();
	private ChainDescription noHBond = makeNO();
	
	private ChainDescription makeON() {
		double minAngle = 145;
		double maxAngle = 180;
		double minDistance = 2.7;
		double maxDistance = 3.4;
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("C", "O");
		GroupDescription groupB = Helper.makeGroupDescription("N");
		chainDescription.addGroupDescriptions(groupA, groupB);
		chainDescription.addAtomListDescriptions(
				new AtomDistanceRangeDescription(minDistance, maxDistance, pathTo(groupA, "O"), pathTo(groupB, "N")),
				new AtomAngleRangeDescription(minAngle, maxAngle, pathTo(groupA, "C"), pathTo(groupA, "O"), pathTo(groupB, "N"))
		);
		return chainDescription;
	}
	
	private ChainDescription makeNO() {
		double minAngle = 145;
		double maxAngle = 180;
		double minDistance = 2.7;
		double maxDistance = 3.0;
		
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("N");
		GroupDescription groupB = Helper.makeGroupDescription("C", "O");
		chainDescription.addGroupDescriptions(groupA, groupB);
		chainDescription.addAtomListDescriptions(
				new AtomDistanceRangeDescription(minDistance, maxDistance, pathTo(groupA, "N"), pathTo(groupB, "O")),
				new AtomAngleRangeDescription(minAngle, maxAngle, pathTo(groupA, "N"), pathTo(groupB, "O"), pathTo(groupB, "C"))
		);
		return chainDescription;
	}
	
	@Test
	public void helixTest() throws IOException {
		String filename = "helix.pdb";
		run(filename, onHBond);
	}
	
	@Test
	public void hairpinTest() throws IOException {
		String filename = "hairpin.pdb";
		run(filename, noHBond);
	}
	
	@Test
	public void betaAlphaBetaTest_NO() throws IOException {
		String filename = "3iwl_clean.pdb";
		run(filename, noHBond);
	}
	
	@Test
	public void betaAlphaBetaTest_ON() throws IOException {
		String filename = "3iwl_clean.pdb";
		run(filename, onHBond);
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
