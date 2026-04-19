package tailor.experiment.test;

import org.junit.Test;

import tailor.engine.plan.Result;

public class TestResultMerging {
	
	@Test
	public void testConcat() {
		Result left = ResultBuilder.result().withGroups("GLY", "SER").withGroupNumbers(1, 3).withAtom("O").build();
		Result right = ResultBuilder.result().withGroups("HIS", "ASP").withGroupNumbers(4, 5).withAtom("O").build();
		testMerge(left, right);
	}
	
	@Test
	public void testContained() {
		Result left = ResultBuilder.result().withGroups("GLY", "SER").withGroupNumbers(1, 4).withAtom("O").build();
		Result right = ResultBuilder.result().withGroups("HIS", "ASP").withGroupNumbers(2, 3).withAtom("O").build();
		testMerge(left, right);
	}
	
	@Test
	public void testOverlap() {
		Result left = ResultBuilder.result().withGroups("GLY", "SER").withGroupNumbers(1, 3).withAtom("O").build();
		Result right = ResultBuilder.result().withGroups("HIS", "ASP").withGroupNumbers(2, 4).withAtom("O").build();
		testMerge(left, right);
	}
	
	private void testMerge(Result left, Result right) {
		System.out.println("Merging " + left + " and " + right);
		boolean isMergable = right.greaterThanOrEqual(left);
		Result merge = left.merge(right);
		System.out.println("Output " + merge);
		System.out.println("Is mergable " + isMergable);
	}

}
