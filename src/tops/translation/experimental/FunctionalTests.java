package tops.translation.experimental;

import static tops.translation.model.BackboneSegment.Type.HELIX;
import static tops.translation.model.BackboneSegment.Type.STRAND;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import tops.translation.model.Chain;
import tops.translation.model.Protein;
import translation.HBondAnalyser;
import translation.PDBReader;

public class FunctionalTests {
	
	private static final String DATA_DIR = "structures";
	
	@Test
	public void testSingleBAB() throws IOException {
		run(bab(), read("2bop.pdb"));
	}
	
	private void run(ChainDescription chainDescription, Protein target) {
		for (Chain chain : target) {
			// create the pipeline
			Plan plan = new Planner().makePlan(chainDescription);	// TODO! reset plan
			SegmentSource segmentSource = new SegmentSource(chain, plan.getInputs());
			PrintResult printer = new PrintResult(plan.getOutputPipe());
			
			// run the pipeline
			segmentSource.run();
			for (Operator op : plan.getOperators()) {
				op.run();
			}
			printer.run();
		}
	}
	
	private ChainDescription bab() {
		double minDistance = 6;
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addSegment(new SegmentDescription(STRAND));
		chainDescription.addSegment(new SegmentDescription(HELIX));
		chainDescription.addSegment(new SegmentDescription(STRAND));
		chainDescription.addSegmentListDescription(
				new SegmentCentroidDistance(
						minDistance, getPathTo(chainDescription, 0), getPathTo(chainDescription, 2)));
		
		return chainDescription;
	}
	
	private SegmentDescriptionPath getPathTo(ChainDescription chainDescription, int segmentDescriptionIndex) {
		return new SegmentDescriptionPath(chainDescription, chainDescription.getSegments().get(segmentDescriptionIndex));
	}
	
	private Protein read(String filename) throws IOException {
		Protein protein = PDBReader.read(new File(DATA_DIR, filename));
		HBondAnalyser hbondAnalyser = new HBondAnalyser();
		hbondAnalyser.analyse(protein);
		return protein;
	}

}
