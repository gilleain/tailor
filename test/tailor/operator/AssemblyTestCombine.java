package tailor.operator;

import java.util.List;

import org.junit.Test;

import tailor.engine.operator.CombineResults;
import tailor.engine.operator.GroupSource;
import tailor.engine.operator.PrintResults;
import tailor.engine.operator.ResultPipe;
import tailor.engine.operator.ScanAtomResultByLabel;
import tailor.structure.Chain;

public class AssemblyTestCombine {
	
	/**
	 * - Scan for 'O' atoms and 'N' atoms
	 * - Combine these into (O, N) pairs
	 */ 
	@Test
	public void testCombineResultsToPairs() {
		PrintResults sink = new PrintResults();
		Chain chain = Helper.makeData(3);
		ResultPipe resultPipe1 = new ResultPipe();
		ResultPipe resultPipe2 = new ResultPipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1, resultPipe2));

		ResultPipe oPipe = new ResultPipe();
		ScanAtomResultByLabel scanO = new ScanAtomResultByLabel(List.of("O"));
		scanO.setSource(resultPipe1);
		scanO.setSink(oPipe);
		
		ResultPipe nPipe = new ResultPipe();
		ScanAtomResultByLabel scanN = new ScanAtomResultByLabel(List.of("N"));
		scanN.setSource(resultPipe2);
		scanN.setSink(nPipe);
		
		CombineResults combineON = new CombineResults(List.of(oPipe, nPipe), sink);
		
		// run the pipeline
		Helper.runAll(List.of(groupSource, scanO, scanN, combineON));
	}
	
	/**
	 * - Scan for 'O' atoms and 'N' atoms
	 * - Combine these into (O, N) pairs
	 * - Scan for 'CA' atoms
	 * - Combine these with the pairs into triples
	 */
	@Test
	public void testCombineResultsToTriples() {
		PrintResults sink = new PrintResults();
		Chain chain = Helper.makeData(3);
		ResultPipe resultPipe1 = new ResultPipe();
		ResultPipe resultPipe2 = new ResultPipe();
		ResultPipe resultPipe3 = new ResultPipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1, resultPipe2, resultPipe3));

		ResultPipe oPipe = new ResultPipe();
		ScanAtomResultByLabel scanO = new ScanAtomResultByLabel(List.of("O"));
		scanO.setSource(resultPipe1);
		scanO.setSink(oPipe);
		
		ResultPipe nPipe = new ResultPipe();
		ScanAtomResultByLabel scanN = new ScanAtomResultByLabel(List.of("N"));
		scanN.setSource(resultPipe2);
		scanN.setSink(nPipe);
		
		ResultPipe resultON = new ResultPipe();
		CombineResults combineON = new CombineResults(List.of(oPipe, nPipe), resultON);
		
		ResultPipe caPipe = new ResultPipe();
		ScanAtomResultByLabel scanCA = new ScanAtomResultByLabel(List.of("CA"));
		scanCA.setSource(resultPipe3);
		scanCA.setSink(caPipe);
		
		CombineResults combineONCa = new CombineResults(List.of(resultON, caPipe), sink);
		
		// run the pipeline
		Helper.runAll(List.of(groupSource, scanO, scanN, combineON, scanCA, combineONCa));
	}
	
	/**
	 * - Scan for 'N' and 'CA' atom pairs
	 * - Scan for 'C' and 'O' atom pairs
	 * - Combine these pairs into quads
	 */
	@Test
	public void testCombineAtomLists() {
		PrintResults sink = new PrintResults();
		Chain chain = Helper.makeData(3);
		ResultPipe resultPipe1 = new ResultPipe();
		ResultPipe resultPipe2 = new ResultPipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1, resultPipe2));

		ResultPipe n_ca_Pipe = new ResultPipe();
		ScanAtomResultByLabel scanNCa = new ScanAtomResultByLabel(List.of("N", "CA"));
		scanNCa.setSource(resultPipe1);
		scanNCa.setSink(n_ca_Pipe);
		
		ResultPipe c_o_Pipe = new ResultPipe();
		ScanAtomResultByLabel scanCO = new ScanAtomResultByLabel(List.of("C", "O"));
		scanCO.setSource(resultPipe2);
		scanCO.setSink(c_o_Pipe);
		
		CombineResults combineNCACO = new CombineResults(List.of(n_ca_Pipe, c_o_Pipe), sink);
		
		// run the pipeline
		Helper.runAll(List.of(groupSource, scanNCa, scanCO, combineNCACO));
	}
	
}
