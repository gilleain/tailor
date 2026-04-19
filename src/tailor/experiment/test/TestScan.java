package tailor.experiment.test;

import java.util.List;

import org.junit.Test;

import tailor.engine.operator.GroupSource;
import tailor.engine.operator.PrintResults;
import tailor.engine.operator.ResultPipe;
import tailor.engine.operator.ScanAtomResultByLabel;
import tailor.structure.Chain;

public class TestScan {
	
	/**
	 * - Scan for 'O' atoms
	 */
	@Test
	public void testScanResultsForSingleAtom() {
		PrintResults sink = new PrintResults();
		Chain chain = Helper.makeData(4);
		ResultPipe resultPipe1 = new ResultPipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1));
		ScanAtomResultByLabel scan = new ScanAtomResultByLabel(List.of("O"));
		scan.setSource(resultPipe1);
		scan.setSink(sink);
		Helper.runAll(List.of(groupSource, scan));
	}
	
	
	/**
	 * - Scan for {'C', 'CA', 'N'} atoms
	 */
	@Test
	public void testScanResultsForMultipleAtoms() {
		PrintResults sink = new PrintResults();
		Chain chain = Helper.makeData(4);
		ResultPipe resultPipe1 = new ResultPipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1));
		ScanAtomResultByLabel scan = new ScanAtomResultByLabel(List.of("C", "CA", "N"));
		scan.setSource(resultPipe1);
		scan.setSink(sink);
		Helper.runAll(List.of(groupSource, scan));
	}

}
