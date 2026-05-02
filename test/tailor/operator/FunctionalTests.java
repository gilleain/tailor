package tailor.operator;

import static tailor.operator.Helper.pathTo;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import tailor.api.AtomListDescription;
import tailor.api.AtomListMeasure;
import tailor.datasource.PDBReader;
import tailor.description.AtomValueRangeDescription;
import tailor.description.ChainDescription;
import tailor.description.DescriptionFactory;
import tailor.description.GroupDescription;
import tailor.description.atom.AtomAngleRangeDescription;
import tailor.description.atom.AtomDistanceRangeDescription;
import tailor.description.atom.AtomTorsionRangeDescription;
import tailor.description.atom.HBondDescription;
import tailor.description.group.GroupSequenceDescription;
import tailor.engine.plan.Plan;
import tailor.engine.plan.Planner;
import tailor.measure.AtomDistanceMeasure;
import tailor.measure.AtomTorsionMeasure;
import tailor.structure.Chain;
import tailor.structure.Structure;

public class FunctionalTests {
	
	private static final String DATA_DIR = "data";
	
	private ChainDescription hairpinTurn() {
		double minAngle = 100;	
		double maxAngle = 180;
		double maxDistance = 2.5; 
		DescriptionFactory factory = new DescriptionFactory("A");
		factory.setAddBackboneAmineHydrogens(true);
		factory.addResiduesAsSegment(5);
		
		ChainDescription chain = factory.getChainDescription("A");
		chain.addAtomListDescriptions(
//			factory.listDescriptions().createHBondDescription("A", maxDistance, minAngle, maxAngle, 0, 4, "i.CO->i+4.HN"),
//			factory.listDescriptions().createHBondDescription("A", maxDistance, minAngle, maxAngle, 0, 5, "i.CO->i+5.HN")
		);
		chain.addAtomListMeasures(
			factory.measures().createPhiMeasure("A", 1, "phi1"),
			factory.measures().createPsiMeasure("A", 1, "psi1"),
			factory.measures().createPhiMeasure("A", 2, "phi2"),
			factory.measures().createPsiMeasure("A", 2, "psi2"),
			factory.measures().createPhiMeasure("A", 3, "phi3"),
			factory.measures().createPsiMeasure("A", 3, "psi3"),
			factory.measures().createHBondMeasure("A", 3, 0),	//	i+3.CO->iHN
			factory.measures().createHBondMeasure("A", 0, 4)    //	i.CO->i+4HN
		);
		return chain;
	}
	
	private ChainDescription makeHBondON() {
		double minAngle = 120;	// Generous angle
		double maxAngle = 180;
		double maxDistance = 2.5; // this is a very long H-A distance!
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("C", "O");
		GroupDescription groupB = Helper.makeGroupDescription("H", "N", "CA", "C");
		chainDescription.addGroupDescriptions(groupA, groupB);
//		System.out.println("Making hbond");
		HBondDescription hBond = new HBondDescription(
				maxDistance, minAngle, maxAngle, 
				pathTo(groupA, "C"), pathTo(groupA, "O"), pathTo(groupB, "H"), pathTo(groupB, "N"));
//		System.out.println("Making measure");
		AtomListMeasure phi = new AtomTorsionMeasure(
				pathTo(groupA, "C"), pathTo(groupB, "N"), pathTo(groupB, "CA"), pathTo(groupB, "C")
		);
		chainDescription.addAtomListDescriptions(hBond);
		chainDescription.addAtomListMeasures(hBond.createMeasure(), phi);
		return chainDescription;
	}
	
	private ChainDescription makeHBondNO() {
		double minAngle = 120;	// Generous angle
		double maxAngle = 180;
		double maxDistance = 2.5;	// this is a very long H-A distance!
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescription("H", "N", "C");
		GroupDescription groupB = Helper.makeGroupDescription("C", "O", "N", "CA");
		chainDescription.addGroupDescriptions(groupA, groupB);
//		System.out.println("Making hbond");
		HBondDescription hBond = new HBondDescription(
				maxDistance, minAngle, maxAngle, 
				pathTo(groupA, "N"), pathTo(groupA, "H"), pathTo(groupB, "O"), pathTo(groupB, "C"));
//		System.out.println("Making measure");
		AtomListMeasure phi = new AtomTorsionMeasure(
				pathTo(groupA, "C"), pathTo(groupB, "N"), pathTo(groupB, "CA"), pathTo(groupB, "C")
		);
		chainDescription.addAtomListDescriptions(hBond);
		chainDescription.addAtomListMeasures(hBond.createMeasure(), phi);
		return chainDescription;
	}
	
	private ChainDescription makeON() {
		return makeON(null, null);
	}
	
	private ChainDescription makeON(String groupAName, String groupBName) {
		double minAngle = 145;
		double maxAngle = 180;
		double minDistance = 2.7;
		double maxDistance = 3.4;
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescriptionWithName(groupAName, "C", "O");
		GroupDescription groupB = Helper.makeGroupDescriptionWithName(groupBName, "N");
		chainDescription.addGroupDescriptions(groupA, groupB);
		AtomListDescription distance = 
				new AtomValueRangeDescription(minDistance, maxDistance, 
						new AtomDistanceMeasure(pathTo(groupA, "O"), pathTo(groupB, "N")));
		AtomListDescription angle = 
				new AtomAngleRangeDescription(minAngle, maxAngle, pathTo(groupA, "C"), pathTo(groupA, "O"), pathTo(groupB, "N"));
		chainDescription.addAtomListDescriptions(distance, angle);
		chainDescription.addAtomListMeasures(distance.createMeasure(), angle.createMeasure());
		return chainDescription;
	}
	
	private ChainDescription makeNO() {
		return this.makeNO(null, null);
	}
	
	private ChainDescription makeNO(String groupAName, String groupBName) {
		double minAngle = 145;
		double maxAngle = 180;
		double minDistance = 2.7;
		double maxDistance = 3.0;
		
		ChainDescription chainDescription = new ChainDescription();
		GroupDescription groupA = Helper.makeGroupDescriptionWithName(groupAName, "N");
		GroupDescription groupB = Helper.makeGroupDescriptionWithName(groupBName, "C", "O");
		chainDescription.addGroupDescriptions(groupA, groupB);
		AtomListDescription distance =
				new AtomDistanceRangeDescription(minDistance, maxDistance, pathTo(groupA, "N"), pathTo(groupB, "O"));
		AtomListDescription angle =
				new AtomAngleRangeDescription(minAngle, maxAngle, pathTo(groupA, "N"), pathTo(groupB, "O"), pathTo(groupB, "C"));
		chainDescription.addAtomListDescriptions(distance, angle);
		chainDescription.addAtomListMeasures(distance.createMeasure(), angle.createMeasure());
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
		chainDescription.addGroupSequenceDescriptions(
				new GroupSequenceDescription(groupA, groupB, 1), new GroupSequenceDescription(groupB, groupC, 1));
		AtomListDescription phi = new AtomTorsionRangeDescription(
				minPhiAngle, maxPhiAngle, pathTo(groupA, "C"), pathTo(groupB, "N"), pathTo(groupB, "CA"), pathTo(groupB, "C")
		);
		AtomListDescription psi = new AtomTorsionRangeDescription(
				minPsiAngle, maxPsiAngle, pathTo(groupB, "N"), pathTo(groupB, "CA"), pathTo(groupB, "C"), pathTo(groupC, "N")
		);
		chainDescription.addAtomListDescriptions(phi, psi);
		chainDescription.addAtomListMeasures(phi.createMeasure(), psi.createMeasure());
		return chainDescription;
	}
	
	@Test
	public void helixONBondTest() throws IOException {
		String filename = "helix.pdb";
		run(filename, makeON(null, "LEU"));
	}
	
	@Test
	public void helixPhiPsiTest() throws IOException {
		String filename = "helix.pdb";
		run(filename, makeAlphaPhiPsi());
	}
	
	@Test
	public void helixHBondTest() throws IOException {
		String filename = "helix.pdb";
		run(filename, makeHBondON());
		run(filename, makeHBondNO());
	}
	
	@Test
	public void hairpinNOBondTest() throws IOException {
		String filename = "hairpin.pdb";
		run(filename, makeNO("VAL", null));
	}
	
	@Test
	public void hairpinTurnTest() throws IOException {
		String filename = "hairpin.pdb";
		run(filename, hairpinTurn());
	}
	
	@Test
	public void hairpinHBondTest() throws IOException {
		String filename = "hairpin.pdb";
		run(filename, makeHBondON());
		run(filename, makeHBondNO());
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
