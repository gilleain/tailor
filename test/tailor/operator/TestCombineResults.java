package tailor.operator;

import static org.junit.Assert.assertEquals;
import static tailor.operator.ResultBuilder.result;
import static tailor.operator.ResultBuilder.resultList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tailor.engine.operator.CombineResults;
import tailor.engine.operator.PrintAdapter;
import tailor.engine.operator.Pipe;
import tailor.engine.plan.Result;
import tailor.operator.ResultBuilder.ResultGroupBuilder;
import tailor.operator.ResultBuilder.ResultGroupListBuilder;

public class TestCombineResults {
	
	private static final String ID = "XXX";
	
	/**
	 * Combine single 'paths' into pairs.
	 */
	@Test
	public void testCombineSingles() {
		Pipe input1 = put(resultList().withGroups("GLY", "SER", "ASP").withAtom("N"));
		Pipe input2 = put(resultList().withGroups("GLY", "SER", "ASP").withAtom("O"));
		Pipe output = new Pipe();
		
		CombineResults combine = new CombineResults(List.of(input1, input2), output);
		combine.run();
		
//		PrintAdapter printer = new PrintAdapter("", output);
//		printer.run();
		List<Result> finalResults = new ArrayList<>();
		CaptorAdapter captor = new CaptorAdapter(output, finalResults);
		captor.run();
		assertEquals("3 results", 3, finalResults.size());
	}
	
	/**
	 * Combine 'pairs' of similar group/atom paths into quads.
	 */
	@Test
	public void combineSimilarPairs() {
		Pipe input1 = put(
				result().startAt(1).withGroups("GLY", "SER").withAtoms("N", "O"),
				result().startAt(2).withGroups("SER", "ASP").withAtoms("N", "O"),
				result().startAt(3).withGroups("ASP", "HIS").withAtoms("N", "O"));
		Pipe input2 = put(
				result().startAt(1).withGroups("GLY", "SER").withAtoms("O", "N"),
				result().startAt(2).withGroups("SER", "ASP").withAtoms("O", "N"),
				result().startAt(3).withGroups("ASP", "HIS").withAtoms("O", "N"));
		Pipe output = new Pipe();
		
		CombineResults combine = new CombineResults(List.of(input1, input2), output);
		combine.run();
		
		PrintAdapter printer = new PrintAdapter("", output);
		printer.run();
	}
	
	/**
	 * Combine 'pairs' of non-similar group/atom paths into quads.
	 */
	@Test
	public void combineDisimilarPairs() {
		Pipe input1 = put(
				result().startAt(1).withGroups("GLY", "SER").withAtoms("N", "C"),
				result().startAt(2).withGroups("SER", "ASP").withAtoms("N", "C"),
				result().startAt(3).withGroups("ASP", "HIS").withAtoms("N", "C"));
		Pipe input2 = put(
				result().startAt(1).withGroups("GLY", "SER").withAtoms("O", "CA"),
				result().startAt(2).withGroups("SER", "ASP").withAtoms("O", "CA"),
				result().startAt(3).withGroups("ASP", "HIS").withAtoms("O", "CA"));
		Pipe output = new Pipe();
		
		CombineResults combine = new CombineResults(List.of(input1, input2), output);
		combine.run();
		
		PrintAdapter printer = new PrintAdapter("", output);
		printer.run();
	}
	
	private Pipe put(ResultGroupListBuilder builder) {
		Pipe pipe = new Pipe();
		builder.build().stream().forEach(pipe::put);
		return pipe;
	}
	
	private Pipe put(ResultGroupBuilder... builders) {
		Pipe pipe = new Pipe();
		for (ResultGroupBuilder builder : builders) {
			pipe.put(builder.build());
		}
		return pipe;
	}
}
