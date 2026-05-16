package tailor.operator;

import java.util.List;

import org.junit.Test;

import tailor.engine.operator.GroupSource;
import tailor.engine.operator.Pipe;
import tailor.engine.operator.PrintAdapter;
import tailor.engine.operator.ScanAtomResultByLabel;
import tailor.structure.Chain;

public class TestScan {
	
	/**
	 * - Scan for 'O' atoms
	 */
	@Test
	public void testScanResultsForSingleAtom() {
		Chain chain = Helper.makeData(4);
		Pipe resultPipe1 = new Pipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1));
		ScanAtomResultByLabel scan = new ScanAtomResultByLabel(List.of("O"));
		scan.setInput(resultPipe1);
		Pipe output = new Pipe();
		scan.setOutput(output);
		Helper.runAll(List.of(groupSource, scan, new PrintAdapter(output)));
	}
	
	
	/**
	 * - Scan for {'C', 'CA', 'N'} atoms
	 */
	@Test
	public void testScanResultsForMultipleAtoms() {
		Chain chain = Helper.makeData(4);
		Pipe resultPipe1 = new Pipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1));
		ScanAtomResultByLabel scan = new ScanAtomResultByLabel(List.of("C", "CA", "N"));
		scan.setInput(resultPipe1);
		Pipe output = new Pipe();
		scan.setOutput(output);
		Helper.runAll(List.of(groupSource, scan, new PrintAdapter(output)));
	}

}
