package tailor.engine.rewrite;

/**
 * Rewrite rule for graphs.
 */
public interface Rule {

	/**
	 * Apply the rule to the graph at this match.
	 * 
	 * @param match the context to apply the rule
	 * @param graph to apply the rule to
	 * @return the input graph transformed
	 */
	public Graph apply(Match match, Graph graph);
}
