package tailor.operator;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import tailor.condition.AtomMatcher;
import tailor.condition.AtomMatcher.Match;
import tailor.condition.AtomPartition;
import tailor.condition.LabelPartition;
import tailor.condition.LabelPartition.LabelledPart;
import tailor.structure.Atom;

public class TestPartitionMatching {

	@Test
	public void testLeftBlank() {
		LabelPartition q = new LabelPartition(List.of(pl(1, "O"), pl(2, "C")));
		AtomPartition t = new AtomPartition(
				List.of(pa(new Atom("N")), 
						pa(new Atom("N"), new Atom("O")), 
						pa(new Atom("N"), new Atom("C"))));
		
		verify(q, t);
	}

	@Test
	public void testMiddleBlank() {
		LabelPartition q = new LabelPartition(List.of(pl(0, "N"), pl(2, "C")));
		AtomPartition t = new AtomPartition(
				List.of(pa(new Atom("N")), 
						pa(new Atom("N"), new Atom("O")), 
						pa(new Atom("C"))));
		
		verify(q, t);
	}
	
	@Test
	public void testRightBlank() {
		LabelPartition q = new LabelPartition(List.of(pl(0, "N"), pl(1, "O")));
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
	
	private LabelledPart pl(int index, String... labels) {
		return new LabelledPart(index, Arrays.asList(labels));
	}
	
	private List<Atom> pa(Atom... atoms) {
		return Arrays.asList(atoms);
	}

}
