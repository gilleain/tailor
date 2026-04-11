package tailor.experiment.test;

import static tailor.experiment.test.Helper.pathTo;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import tailor.datasource.PDBReader;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.description.atom.AtomAngleRangeDescription;
import tailor.experiment.description.atom.AtomDistanceRangeDescription;
import tailor.experiment.description.atom.AtomTorsionRangeDescription;
import tailor.experiment.plan.Plan;
import tailor.experiment.plan.Planner;
import tailor.structure.Chain;
import tailor.structure.Structure;

public class FunctionalTests {
	
	private static final String DATA_DIR = "data";
	
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
	
	private ChainDescription makeAlphaPhiPsi() {
		double minPhiAngle = -71;
		double maxPhiAngle = -57;
		double minPsiAngle = -48;
		double maxPsiAngle = -34;
		
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("C");
		GroupDescription groupB = Helper.makeGroupDescription("N", "CA", "C");
		GroupDescription groupC = Helper.makeGroupDescription("N");
		chainDescription.addGroupDescriptions(groupA, groupB, groupC);
		chainDescription.addAtomListDescriptions(
				new AtomTorsionRangeDescription(
					minPhiAngle, maxPhiAngle, pathTo(groupA, "C"), pathTo(groupB, "N"), pathTo(groupB, "CA"), pathTo(groupB, "C")
				),
				new AtomTorsionRangeDescription(
					minPsiAngle, maxPsiAngle, pathTo(groupB, "N"), pathTo(groupB, "CA"), pathTo(groupB, "C"), pathTo(groupC, "N")
				)
		);
		return chainDescription;
	}
	
	@Test
	public void helixONBondTest() throws IOException {
		String filename = "helix.pdb";
		run(filename, makeON());
	}
	
	@Test
	public void helixPhiPsiTest() throws IOException {
		String filename = "helix.pdb";
		run(filename, makeAlphaPhiPsi());
	}
	
	@Test
	public void hairpinNOBondTest() throws IOException {
		String filename = "hairpin.pdb";
		run(filename, makeNO());
	}
	
	@Test
	public void betaAlphaBetaTest_NOBond() throws IOException {
		String filename = "3iwl_clean.pdb";
		run(filename, makeNO());
	}
	
	@Test
	public void betaAlphaBetaTest_ONBond() throws IOException {
		String filename = "3iwl_clean.pdb";
		run(filename, makeON());
	}
	
	@Test
	public void betaAlphaBetaTest_PhiPsi() throws IOException {
		String filename = "3iwl_clean.pdb";
		run(filename, makeAlphaPhiPsi());
	}
	
	private void run(String filename, ChainDescription chainDescription) throws IOException {
		Plan plan = new Planner().plan(chainDescription);
		plan.describe();
		
		Structure structure = PDBReader.read(new File(DATA_DIR, filename));
		for (Structure chainStructure : structure.getSubstructures()) {
			Chain chain = (Chain) chainStructure;
			// TODO - do we have to reset?
			Helper.run(chain, plan);
		}
	}

}
