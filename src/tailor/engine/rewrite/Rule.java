package tailor.engine.rewrite;

/**
 * Rewrite rule for graphs.
 */
public interface Rule {
	
	/**
	 * Convert the attributes of a match into new attributes to 
	 * set in the fresh parts of the new graph, transforming them 
	 * along the way.
	 * 
	 * @param oldAttributes attributes of the graph we are matching against
	 * @return the transformed attributes
	 */
	public Attributes operate(Attributes oldAttributes);

	/**
	 * @return the pattern graph that this rule looks for in the target graph.
	 */
	public Graph getLGraph();

	public Graph getIGraph();
}
