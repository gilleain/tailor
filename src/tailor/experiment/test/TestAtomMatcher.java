package tailor.experiment.test;

import static tailor.experiment.test.ResultBuilder.result;

import java.util.List;

import org.junit.Test;

import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.condition.AtomMatcher.Match;
import tailor.experiment.plan.Result;

public class TestAtomMatcher {
	
	/**
	 * Input result has one group with distinct atoms where all match.
	 */
	@Test
	public void testAllMatchDistinctAtomsInOneGroup() {
		Result resultInput = result().withGroups("ASP").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of("N", "CA", "C", "O"));
		for (Match match : matcher.extract(resultInput)) {
			System.out.println(match);
		}
	}
	
	/**
	 * Input result has one group with distinct atoms where some match.
	 */
	@Test
	public void testPartialMatchDistinctAtomsInOneGroup() {
		Result resultInput = result().withGroups("ASP").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of("C", "O"));
		for (Match match : matcher.extract(resultInput)) {
			System.out.println(match);
		}
	}
	
	/**
	 * Input result has multiple groups with distinct atoms.
	 */
	@Test
	public void testAllMatchDistinctAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of("N", "CA", "C", "O"));
		for (Match match : matcher.extract(resultInput)) {
			System.out.println(match);
		}
	}
	
	/**
	 * Input result has multiple groups with distinct atoms where some match.
	 */
	@Test
	public void testPartialMatchDistinctAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of("CA", "O"));
		for (Match match : matcher.extract(resultInput)) {
			System.out.println(match);
		}
	}
	
	/**
	 * Input result has multiple groups with same atoms.
	 */
	@Test
	public void testAllMatchSameAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("O", "O", "O", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of("O", "O", "O", "O"));
		for (Match match : matcher.extract(resultInput)) {
			System.out.println(match);
		}
	}
	
	/**
	 * Input result has multiple groups with distinct atoms where some match.
	 */
	@Test
	public void testPartialMatchSameAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("O", "O", "O", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of("O", "O"));
		for (Match match : matcher.extract(resultInput)) {
			System.out.println(match);
		}
	}
	

}
