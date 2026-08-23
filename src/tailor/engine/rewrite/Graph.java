package tailor.engine.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple labeled, undirected graph - used both as the pattern held by a
 * {@link Rule} and as the target graph that a {@link Rewriter} transforms.
 *
 * Vertices are identified by their index (0-based, in the order they were
 * added). Vertex and edge labels are compared by {@link VF2Matcher} to
 * decide whether a pattern vertex/edge is compatible with a target
 * vertex/edge - a label of <code>null</code> acts as a wildcard, matching
 * anything.
 */
public class Graph {

	private final List<String> vertexLabels = new ArrayList<>();
	private final List<Set<Integer>> adjacency = new ArrayList<>();
	private final Map<String, String> edgeLabels = new HashMap<>();

	public int addVertex(String label) {
		int index = vertexLabels.size();
		vertexLabels.add(label);
		adjacency.add(new LinkedHashSet<>());
		return index;
	}

	public int addVertex() {
		return addVertex(null);
	}

	public void addEdge(int a, int b) {
		addEdge(a, b, null);
	}

	public void addEdge(int a, int b, String label) {
		checkVertex(a);
		checkVertex(b);
		adjacency.get(a).add(b);
		adjacency.get(b).add(a);
		edgeLabels.put(edgeKey(a, b), label);
	}

	public int vertexCount() {
		return vertexLabels.size();
	}

	public String vertexLabel(int vertex) {
		checkVertex(vertex);
		return vertexLabels.get(vertex);
	}

	public Set<Integer> neighbors(int vertex) {
		checkVertex(vertex);
		return Collections.unmodifiableSet(adjacency.get(vertex));
	}

	public boolean hasEdge(int a, int b) {
		checkVertex(a);
		checkVertex(b);
		return adjacency.get(a).contains(b);
	}

	public String edgeLabel(int a, int b) {
		return edgeLabels.get(edgeKey(a, b));
	}

	public int degree(int vertex) {
		checkVertex(vertex);
		return adjacency.get(vertex).size();
	}

	private void checkVertex(int vertex) {
		if (vertex < 0 || vertex >= vertexLabels.size()) {
			throw new IndexOutOfBoundsException("No such vertex : " + vertex);
		}
	}

	private static String edgeKey(int a, int b) {
		return a < b ? (a + ":" + b) : (b + ":" + a);
	}
}
