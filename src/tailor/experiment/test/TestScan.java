package tailor.experiment.test;

import java.util.List;

import org.junit.Test;

import tailor.experiment.operator.GroupPipe;
import tailor.experiment.operator.PrintAtomLists;
import tailor.experiment.operator.PrintAtoms;
import tailor.experiment.operator.ScanAtomByLabel;
import tailor.experiment.operator.ScanAtomListsByLabel;
import tailor.structure.Chain;

public class TestScan {
	
	/**
	 * - Scan for 'O' atoms
	 */
	@Test
	public void testScanAtoms() {
		PrintAtoms sink = new PrintAtoms();
		Chain chain = Helper.makeData();
		ScanAtomByLabel scan = new ScanAtomByLabel("O", new GroupPipe(chain), sink);
		scan.run();
	}
	
	/**
	 * - Scan for {'C', 'CA', 'N'} atoms
	 */
	@Test
	public void testScanAtomLists() {
		PrintAtomLists sink = new PrintAtomLists();
		Chain chain = Helper.makeData();
		ScanAtomListsByLabel scan = new ScanAtomListsByLabel(List.of("C", "CA", "N"), new GroupPipe(chain), sink);
		scan.run();
	}

}
