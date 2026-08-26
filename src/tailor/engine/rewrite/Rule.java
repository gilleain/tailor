package tailor.engine.rewrite;

/**
 * Rewrite rule for graphs.
 */
public interface Rule {

	/**
	 * @return the pattern graph that this rule looks for in the target graph.
	 */
	public Graph pattern();

	/**
	 * Apply the rule to the graph at this match.
	 * 
	 * @param match the context to apply the rule
	 * @param targetGraph the graph to apply the rule to
	 * @return the input graph transformed
	 */
	public Graph apply(Match match, Graph targetGraph);
}
