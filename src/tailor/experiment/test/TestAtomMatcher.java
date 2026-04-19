package tailor.experiment.test;

import static org.junit.Assert.assertTrue;
import static tailor.experiment.test.ResultBuilder.result;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import tailor.condition.AtomMatcher;
import tailor.condition.AtomMatcher.Match;
import tailor.engine.plan.Result;
import tailor.condition.LabelPartition;

public class TestAtomMatcher {
	
	/**
	 * Input result has one group with distinct atoms where all match.
	 */
	@Test
	public void testAllMatchDistinctAtomsInOneGroup() {
		Result resultInput = result().withGroups("ASP").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(new LabelPartition(List.of(List.of("N", "CA", "C", "O"))));
		Optional<Match> match = matcher.containedIn(resultInput.getAtomPartition());
		assertTrue(match.isPresent());
		System.out.println(match.get());
	}
	
	/**
	 * Input result has one group with distinct atoms where some match.
	 */
	@Test
	public void testPartialMatchDistinctAtomsInOneGroup() {
		Result resultInput = result().withGroups("ASP").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(new LabelPartition(List.of(List.of("C", "O"))));
		Optional<Match> match = matcher.containedIn(resultInput.getAtomPartition());
		assertTrue(match.isPresent());
		System.out.println(match.get());
	}
	
	/**
	 * Input result has multiple groups with distinct atoms.
	 */
	@Test
	public void testAllMatchDistinctAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(
				new LabelPartition(List.of(List.of("N"), List.of("CA"), List.of("C"), List.of("O"))));
		Optional<Match> match = matcher.containedIn(resultInput.getAtomPartition());
		assertTrue(match.isPresent());
		System.out.println(match.get());
	}
	
	@Test
	public void testAllMatchDistinctSetsAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY").withAtoms("C", "O")
									 .withGroups("ASP").withAtom("N").build();
		AtomMatcher matcher = new AtomMatcher(
				new LabelPartition(List.of(List.of("C", "O"), List.of("N"))));
		Optional<Match> match = matcher.containedIn(resultInput.getAtomPartition());
		assertTrue(match.isPresent());
		System.out.println(match.get());
	}
	
	/**
	 * Input result has multiple groups with distinct atoms where some match.
	 */
	@Test
	public void testPartialMatchDistinctAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("N", "CA", "C", "O").build();
		AtomMatcher matcher = new AtomMatcher(new LabelPartition(List.of(List.of("CA"), List.of("O"))));
		Optional<Match> match = matcher.containedIn(resultInput.getAtomPartition());
		assertTrue(match.isPresent());
		System.out.println(match.get());
	}
	
	/**
	 * Input result has multiple groups with same atoms.
	 */
	@Test
	public void testAllMatchSameAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("O", "O", "O", "O").build();
		AtomMatcher matcher = new AtomMatcher(
				new LabelPartition(List.of(List.of("O"), List.of("O"), List.of("O"), List.of("O"))));
		Optional<Match> match = matcher.containedIn(resultInput.getAtomPartition());
		assertTrue(match.isPresent());
		System.out.println(match.get());
	}
	
	/**
	 * Input result has multiple groups with distinct atoms where some match.
	 */
	@Test
	public void testPartialMatchSameAtomsInMultipleGroups() {
		Result resultInput = result().withGroups("GLY", "ASP", "SER", "HIS").withAtoms("O", "O", "O", "O").build();
		AtomMatcher matcher = new AtomMatcher(
				new LabelPartition(List.of(List.of("O"), List.of("O"))));
		Optional<Match> match = matcher.containedIn(resultInput.getAtomPartition());
		assertTrue(match.isPresent());
		System.out.println(match.get());
	}
}
