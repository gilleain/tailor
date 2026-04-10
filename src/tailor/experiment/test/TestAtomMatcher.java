package tailor.experiment.test;

import static org.junit.Assert.assertEquals;
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
		AtomMatcher matcher = new AtomMatcher(List.of(List.of("N", "CA", "C", "O")));
		List<Match> matches = matcher.extract(resultInput);
		assertEquals(1, matches.size());
		System.out.println(matches.get(0));
	}
	
	/**
	 * Input result has one group with distinct atoms where some match.
	 */
	@Test
	public void testPartialMatchDistinctAtomsInOneGroup() {
		Result resultInput = result().withGroups("ASP").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of(List.of("C", "O")));
		List<Match> matches = matcher.extract(resultInput);
		assertEquals(1, matches.size());
		System.out.println(matches.get(0));
	}
	
	/**
	 * Input result has multiple groups with distinct atoms.
	 */
	@Test
	public void testAllMatchDistinctAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of(List.of("N"), List.of("CA"), List.of("C"), List.of("O")));
		List<Match> matches = matcher.extract(resultInput);
		assertEquals(1, matches.size());
		System.out.println(matches.get(0));
	}
	
	@Test
	public void testAllMatchDistinctSetsAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY").withAtoms("C", "O")
									 .withGroups("ASP").withAtom("N").build();
		AtomMatcher matcher = new AtomMatcher(List.of(List.of("C", "O"), List.of("N")));
		List<Match> matches = matcher.extract(resultInput);
		assertEquals(1, matches.size());
		System.out.println(matches.get(0));
	}
	
	/**
	 * Input result has multiple groups with distinct atoms where some match.
	 */
	@Test
	public void testPartialMatchDistinctAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of(List.of("CA"), List.of("O")));
		List<Match> matches = matcher.extract(resultInput);
		assertEquals(1, matches.size());
		System.out.println(matches.get(0));
	}
	
	/**
	 * Input result has multiple groups with same atoms.
	 */
	@Test
	public void testAllMatchSameAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("O", "O", "O", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of(List.of("O"), List.of("O"), List.of("O"), List.of("O")));
		List<Match> matches = matcher.extract(resultInput);
		assertEquals(1, matches.size());
		System.out.println(matches.get(0));
	}
	
	/**
	 * Input result has multiple groups with distinct atoms where some match.
	 */
	@Test
	public void testPartialMatchSameAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("O", "O", "O", "O").build();
		AtomMatcher matcher = new AtomMatcher(List.of(List.of("O"), List.of("O")));
		List<Match> matches = matcher.extract(resultInput);
		assertEquals(1, matches.size());
		System.out.println(matches.get(0));
	}
}
