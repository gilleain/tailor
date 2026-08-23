package tailor.engine.rewrite;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Match of a rule to a graph, as a set of indexes from
 * the rule vertices to the graph.
 */
public class Match {

	private final Map<Integer, Integer> mapping;

	public Match(Map<Integer, Integer> mapping) {
		this.mapping = Collections.unmodifiableMap(new LinkedHashMap<>(mapping));
	}

	/**
	 * The size of the match, which is just the number of
	 * vertices matched in the target graph.
	 *
	 */
	public int size() {
		return mapping.size();
	}

	/**
	 * @param patternVertex a vertex index in the rule's pattern graph
	 * @return the target graph vertex that patternVertex is mapped to
	 */
	public int get(int patternVertex) {
		Integer targetVertex = mapping.get(patternVertex);
		if (targetVertex == null) {
			throw new IllegalArgumentException("No mapping for pattern vertex " + patternVertex);
		}
		return targetVertex;
	}

	public Map<Integer, Integer> asMap() {
		return mapping;
	}

	@Override
	public String toString() {
		return mapping.toString();
	}

}
