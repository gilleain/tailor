package tailor.engine.rewrite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class TestVF2Matcher {

	/**
	 * A single edge (pattern) should be found inside a triangle (target).
	 */
	@Test
	public void testEdgeMatchesTriangle() {
		Graph pattern = new Graph();
		int p0 = pattern.addVertex();
		int p1 = pattern.addVertex();
		pattern.addEdge(p0, p1);

		Graph target = new Graph();
		int t0 = target.addVertex();
		int t1 = target.addVertex();
		int t2 = target.addVertex();
		target.addEdge(t0, t1);
		target.addEdge(t1, t2);
		target.addEdge(t2, t0);

		Optional<Match> match = new VF2Matcher(pattern, target).matchFirst();

		assertTrue(match.isPresent());
		assertEquals(2, match.get().size());
		assertTrue(target.hasEdge(match.get().get(p0), match.get().get(p1)));
	}

	/**
	 * A triangle (pattern) does not occur inside a simple path (target).
	 */
	@Test
	public void testTriangleDoesNotMatchPath() {
		Graph pattern = new Graph();
		int p0 = pattern.addVertex();
		int p1 = pattern.addVertex();
		int p2 = pattern.addVertex();
		pattern.addEdge(p0, p1);
		pattern.addEdge(p1, p2);
		pattern.addEdge(p2, p0);

		Graph target = new Graph();
		int t0 = target.addVertex();
		int t1 = target.addVertex();
		int t2 = target.addVertex();
		target.addEdge(t0, t1);
		target.addEdge(t1, t2);

		Optional<Match> match = new VF2Matcher(pattern, target).matchFirst();

		assertFalse(match.isPresent());
	}

	/**
	 * Vertex labels restrict which target vertices a pattern vertex can be
	 * mapped to.
	 */
	@Test
	public void testVertexLabelsMustMatch() {
		Graph pattern = new Graph();
		int p0 = pattern.addVertex("A");
		int p1 = pattern.addVertex("B");
		pattern.addEdge(p0, p1);

		Graph target = new Graph();
		int t0 = target.addVertex("B");
		int t1 = target.addVertex("A");
		target.addEdge(t0, t1);

		Optional<Match> match = new VF2Matcher(pattern, target).matchFirst();

		assertTrue(match.isPresent());
		assertEquals(t1, match.get().get(p0));
		assertEquals(t0, match.get().get(p1));
	}

	/**
	 * A triangle pattern has three rotational matches onto a triangle
	 * target of the same shape.
	 */
	@Test
	public void testMatchAllFindsEveryOccurrence() {
		Graph pattern = new Graph();
		int p0 = pattern.addVertex();
		int p1 = pattern.addVertex();
		int p2 = pattern.addVertex();
		pattern.addEdge(p0, p1);
		pattern.addEdge(p1, p2);
		pattern.addEdge(p2, p0);

		Graph target = new Graph();
		int t0 = target.addVertex();
		int t1 = target.addVertex();
		int t2 = target.addVertex();
		target.addEdge(t0, t1);
		target.addEdge(t1, t2);
		target.addEdge(t2, t0);

		List<Match> matches = new VF2Matcher(pattern, target).matchAll();

		assertEquals(6, matches.size());
	}
}
