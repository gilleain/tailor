package tailor.experiment.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.condition.AtomMatcher.Match;
import tailor.experiment.condition.AtomPartition;
import tailor.experiment.condition.LabelPartition;
import tailor.structure.Atom;

public class TestPartitionMatching {

	@Test
	public void testLeftBlank() {
		LabelPartition q = new LabelPartition(List.of(pl(), pl("O"), pl("C")));
		AtomPartition t = new AtomPartition(
				List.of(pa(new Atom("N")), 
						pa(new Atom("N"), new Atom("O")), 
						pa(new Atom("N"), new Atom("C"))));
		
		verify(q, t);
	}

	@Test
	public void testMiddleBlank() {
		LabelPartition q = new LabelPartition(List.of(pl("N"), pl(), pl("C")));
		AtomPartition t = new AtomPartition(
				List.of(pa(new Atom("N")), 
						pa(new Atom("N"), new Atom("O")), 
						pa(new Atom("C"))));
		
		verify(q, t);
	}
	
	@Test
	public void testRightBlank() {
		LabelPartition q = new LabelPartition(List.of(pl("N"), pl("O"), pl()));
		AtomPartition t = new AtomPartition(
				List.of(pa(new Atom("N")), 
						pa(new Atom("O"), new Atom("C")), 
						pa(new Atom("C"))));
		
		verify(q, t);
	}
	
	private void verify(LabelPartition q, AtomPartition t) {
		AtomMatcher m = new AtomMatcher(q);
		Optional<Match> match = m.containedIn(t);
		System.out.println(match);
		assertTrue(match.isPresent());
	}
	
	private List<String> pl(String... labels) {
		return Arrays.asList(labels);
	}
	
	private List<Atom> pa(Atom... atoms) {
		return Arrays.asList(atoms);
	}

}
