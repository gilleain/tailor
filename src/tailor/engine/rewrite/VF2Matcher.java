package tailor.engine.rewrite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Finds occurrences of a (typically small) pattern graph within a target
 * graph, using the VF2 subgraph isomorphism algorithm.
 *
 * A match assigns every pattern vertex to a distinct target vertex, such
 * that vertex labels are compatible and every pattern edge corresponds to
 * an edge in the target graph with a compatible label. The target graph is
 * free to contain additional vertices and edges that are not part of the
 * match - this is the "subgraph matching" used by graph rewriting engines,
 * where a rule's pattern only needs to occur somewhere inside the graph
 * being rewritten.
 *
 * As in the original VF2 algorithm, the search grows a partial mapping one
 * pair of vertices at a time: it prefers pattern vertices that are already
 * adjacent to the mapped portion of the pattern (so the match grows
 * outwards from a connected core), and restricts the target vertices it
 * tries to the neighbors of an already-mapped target vertex wherever
 * possible, before falling back to a feasibility check on every candidate
 * pair.
 */
public class VF2Matcher {

	private final Graph pattern;
	private final Graph target;

	public VF2Matcher(Graph pattern, Graph target) {
		this.pattern = pattern;
		this.target = target;
	}

	/**
	 * @return the first match found, or empty if the pattern does not occur
	 *         anywhere in the target graph.
	 */
	public Optional<Match> matchFirst() {
		List<Match> matches = new ArrayList<>();
		search(newCore(pattern), newCore(target), matches, true);
		return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
	}

	/**
	 * @return every match of the pattern graph onto the target graph.
	 */
	public List<Match> matchAll() {
		List<Match> matches = new ArrayList<>();
		search(newCore(pattern), newCore(target), matches, false);
		return matches;
	}

	private static int[] newCore(Graph graph) {
		int[] core = new int[graph.vertexCount()];
		Arrays.fill(core, -1);
		return core;
	}

	/**
	 * Recursively extends the partial mapping held in core1/core2 by one
	 * more pair of vertices at a time.
	 *
	 * @param core1 pattern vertex -&gt; target vertex, or -1 if unmapped
	 * @param core2 target vertex -&gt; pattern vertex, or -1 if unmapped
	 * @return true if the search should stop (a match was found and only
	 *         the first one was wanted)
	 */
	private boolean search(int[] core1, int[] core2, List<Match> matches, boolean stopAtFirst) {
		if (isComplete(core1)) {
			matches.add(toMatch(core1));
			return stopAtFirst;
		}

		int patternVertex = nextPatternVertex(core1);
		for (int targetVertex : candidateTargetVertices(core1, patternVertex)) {
			if (!isFeasible(core1, core2, patternVertex, targetVertex)) {
				continue;
			}

			core1[patternVertex] = targetVertex;
			core2[targetVertex] = patternVertex;

			if (search(core1, core2, matches, stopAtFirst)) {
				return true;
			}

			core1[patternVertex] = -1;
			core2[targetVertex] = -1;
		}

		return false;
	}

	private static boolean isComplete(int[] core1) {
		for (int targetVertex : core1) {
			if (targetVertex == -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Picks the next pattern vertex to map: preferably one already adjacent
	 * to the mapped portion of the pattern, so that the match grows
	 * outwards from a connected core, as in VF2.
	 */
	private int nextPatternVertex(int[] core1) {
		for (int vertex = 0; vertex < core1.length; vertex++) {
			if (core1[vertex] == -1 && hasMappedNeighbor(core1, vertex)) {
				return vertex;
			}
		}
		for (int vertex = 0; vertex < core1.length; vertex++) {
			if (core1[vertex] == -1) {
				return vertex;
			}
		}
		throw new IllegalStateException("No unmapped pattern vertex left");
	}

	private boolean hasMappedNeighbor(int[] core1, int patternVertex) {
		for (int neighbor : pattern.neighbors(patternVertex)) {
			if (core1[neighbor] != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Candidate target vertices for patternVertex: if it has a neighbor
	 * that is already mapped, only that mapped vertex's target-graph
	 * neighbors can possibly work (any valid match must connect the two),
	 * so the search only tries those. Otherwise every target vertex not
	 * yet used is a candidate.
	 */
	private Iterable<Integer> candidateTargetVertices(int[] core1, int patternVertex) {
		for (int neighbor : pattern.neighbors(patternVertex)) {
			if (core1[neighbor] != -1) {
				return target.neighbors(core1[neighbor]);
			}
		}
		List<Integer> all = new ArrayList<>();
		for (int vertex = 0; vertex < target.vertexCount(); vertex++) {
			all.add(vertex);
		}
		return all;
	}

	private boolean isFeasible(int[] core1, int[] core2, int patternVertex, int targetVertex) {
		if (core2[targetVertex] != -1) {
			return false; // target vertex already used elsewhere in this match
		}
		if (!labelsCompatible(pattern.vertexLabel(patternVertex), target.vertexLabel(targetVertex))) {
			return false;
		}
		for (int patternNeighbor : pattern.neighbors(patternVertex)) {
			int targetNeighbor = core1[patternNeighbor];
			if (targetNeighbor == -1) {
				continue; // not mapped yet - will be checked when it is
			}
			if (!target.hasEdge(targetVertex, targetNeighbor)) {
				return false;
			}
			if (!labelsCompatible(pattern.edgeLabel(patternVertex, patternNeighbor),
					target.edgeLabel(targetVertex, targetNeighbor))) {
				return false;
			}
		}
		return true;
	}

	private static boolean labelsCompatible(String patternLabel, String targetLabel) {
		return patternLabel == null || patternLabel.equals(targetLabel);
	}

	private static Match toMatch(int[] core1) {
		Map<Integer, Integer> mapping = new LinkedHashMap<>();
		for (int patternVertex = 0; patternVertex < core1.length; patternVertex++) {
			mapping.put(patternVertex, core1[patternVertex]);
		}
		return new Match(mapping);
	}
}
