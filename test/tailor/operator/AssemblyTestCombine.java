package tailor.operator;

import java.util.List;

import org.junit.Test;

import tailor.engine.operator.CombineResults;
import tailor.engine.operator.GroupSource;
import tailor.engine.operator.PrintAdapter;
import tailor.engine.operator.Pipe;
import tailor.engine.operator.ScanAtomResultByLabel;
import tailor.structure.Chain;

public class AssemblyTestCombine {
	
	/**
	 * - Scan for 'O' atoms and 'N' atoms
	 * - Combine these into (O, N) pairs
	 */ 
	@Test
	public void testCombineResultsToPairs() {
		Chain chain = Helper.makeData(3);
		Pipe resultPipe1 = new Pipe();
		Pipe resultPipe2 = new Pipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1, resultPipe2));

		Pipe oPipe = new Pipe();
		ScanAtomResultByLabel scanO = new ScanAtomResultByLabel(List.of("O"));
		scanO.setInput(resultPipe1);
		scanO.setOutput(oPipe);
		
		Pipe nPipe = new Pipe();
		ScanAtomResultByLabel scanN = new ScanAtomResultByLabel(List.of("N"));
		scanN.setInput(resultPipe2);
		scanN.setOutput(nPipe);
		
		Pipe output = new Pipe();
		CombineResults combineON = new CombineResults(List.of(oPipe, nPipe), output);
		
		// run the pipeline
		Helper.runAll(List.of(groupSource, scanO, scanN, combineON, new PrintAdapter(output)));
	}
	
	/**
	 * - Scan for 'O' atoms and 'N' atoms
	 * - Combine these into (O, N) pairs
	 * - Scan for 'CA' atoms
	 * - Combine these with the pairs into triples
	 */
	@Test
	public void testCombineResultsToTriples() {
		Chain chain = Helper.makeData(3);
		Pipe resultPipe1 = new Pipe();
		Pipe resultPipe2 = new Pipe();
		Pipe resultPipe3 = new Pipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1, resultPipe2, resultPipe3));

		Pipe oPipe = new Pipe();
		ScanAtomResultByLabel scanO = new ScanAtomResultByLabel(List.of("O"));
		scanO.setInput(resultPipe1);
		scanO.setOutput(oPipe);
		
		Pipe nPipe = new Pipe();
		ScanAtomResultByLabel scanN = new ScanAtomResultByLabel(List.of("N"));
		scanN.setInput(resultPipe2);
		scanN.setOutput(nPipe);
		
		Pipe resultON = new Pipe();
		CombineResults combineON = new CombineResults(List.of(oPipe, nPipe), resultON);
		
		Pipe caPipe = new Pipe();
		ScanAtomResultByLabel scanCA = new ScanAtomResultByLabel(List.of("CA"));
		scanCA.setInput(resultPipe3);
		scanCA.setOutput(caPipe);
		
		Pipe output = new Pipe();
		CombineResults combineONCa = new CombineResults(List.of(resultON, caPipe), output);
		
		// run the pipeline
		Helper.runAll(List.of(groupSource, scanO, scanN, combineON, scanCA, combineONCa, new PrintAdapter(output)));
	}
	
	/**
	 * - Scan for 'N' and 'CA' atom pairs
	 * - Scan for 'C' and 'O' atom pairs
	 * - Combine these pairs into quads
	 */
	@Test
	public void testCombineAtomLists() {
		Chain chain = Helper.makeData(3);
		Pipe resultPipe1 = new Pipe();
		Pipe resultPipe2 = new Pipe();
		GroupSource groupSource = new GroupSource(chain, List.of(resultPipe1, resultPipe2));

		Pipe n_ca_Pipe = new Pipe();
		ScanAtomResultByLabel scanNCa = new ScanAtomResultByLabel(List.of("N", "CA"));
		scanNCa.setInput(resultPipe1);
		scanNCa.setOutput(n_ca_Pipe);
		
		Pipe c_o_Pipe = new Pipe();
		ScanAtomResultByLabel scanCO = new ScanAtomResultByLabel(List.of("C", "O"));
		scanCO.setInput(resultPipe2);
		scanCO.setOutput(c_o_Pipe);
		
		Pipe output = new Pipe();
		CombineResults combineNCACO = new CombineResults(List.of(n_ca_Pipe, c_o_Pipe), output);
		
		// run the pipeline
		Helper.runAll(List.of(groupSource, scanNCa, scanCO, combineNCACO, new PrintAdapter(output)));
	}
	
}
