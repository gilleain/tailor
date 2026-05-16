package tops.translation.experimental;

import static tailor.structure.Segment.Type.HELIX;
import static tailor.structure.Segment.Type.STRAND;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import tailor.structure.Chain;
import tailor.structure.Protein;
import tailor.structure.Segment;
import translation.HBondAnalyser;
import translation.PDBReader;

public class FunctionalTests {
	
	private static final String DATA_DIR = "structures";
	
	@Test
	public void testSingleBAB() throws IOException {
		run(bab(), read("1ajj.pdb"));
	}
	
	@Test
	public void testSingleLongStrand() throws IOException {
		run(longStrand(), read("1a73.pdb"));
	}
	
	private void run(ChainDescription chainDescription, Protein target) {
		for (Chain chain : target.getChains()) {
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
	
	private ChainDescription longStrand() {
		int minLength = 5;
		ChainDescription chainDescription = new ChainDescription();
		SegmentDescription segmentDescription = new SegmentDescription(STRAND);
		chainDescription.addSegment(segmentDescription);
		segmentDescription.addPropertyDescription(new SegmentLength(minLength, getPathTo(chainDescription, 0)));
		
		return chainDescription;
	}
	
	private ChainDescription bab() {
		double minDistance = 13;
		ChainDescription chainDescription = new ChainDescription();
		chainDescription.addSegment(new SegmentDescription(STRAND));
		chainDescription.addSegment(new SegmentDescription(HELIX));
		chainDescription.addSegment(new SegmentDescription(STRAND));
		chainDescription.addSegmentListDescriptions(
				new SegmentCentroidDistance(
						minDistance, getPathTo(chainDescription, 0), getPathTo(chainDescription, 1)
				),
				new SegmentCentroidDistance(
						minDistance, getPathTo(chainDescription, 1), getPathTo(chainDescription, 2)
				)
		);
		
		return chainDescription;
	}
	
	private SegmentDescriptionPath getPathTo(ChainDescription chainDescription, int segmentDescriptionIndex) {
		return new SegmentDescriptionPath(chainDescription, chainDescription.getSegments().get(segmentDescriptionIndex));
	}
	
	private Protein read(String filename) throws IOException {
		Protein protein = PDBReader.read(new File(DATA_DIR, filename));
		HBondAnalyser hbondAnalyser = new HBondAnalyser();
		hbondAnalyser.analyse(protein);
		for (Chain chain : protein.getChains()) {
			for (Segment segment : chain.getSegments()) {
				System.out.println(segment);
			}
		}
		return protein;
	}

}
