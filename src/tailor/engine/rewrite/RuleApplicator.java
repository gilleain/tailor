package tailor.engine.rewrite;

/**
 * Applies a rule to a graph.
 */
public class RuleApplicator {
	
	/**
	 * Apply the rule to the graph at this match.
	 * 
	 * @param rule to apply
	 * @param match the context to apply the rule
	 * @param targetGraph the graph to apply the rule to
	 * @return the input graph transformed
	 */
	public Graph apply(Rule rule, Match match, Graph targetGraph) {
		
		// Determine obsolete parts of G, to make C
		Graph cGraph = makeC(match, targetGraph, rule.getLGraph(), rule.getIGraph());
		// TODO - split above into:
		// a) get obsolete parts
		// b) get old attributes from obsolete parts
		// c) rule.operate(oldAttr) -> newAttr
		// d) pass the newAttr to 'makeH' below
		

		// Determine fresh parts to add to C to make H
		Graph hGraph = makeH(cGraph);
		
		return hGraph;
	}
	
	private Graph makeC(Match match, Graph targetGraph, Graph lGraph, Graph iGraph) {
		Graph cGraph = new Graph(targetGraph);
		
		// TODO - use inversePhiL to bridge the match (?)
		
		// TODO - handle merged or split vertices
		
		for (int lVx = 0; lVx < lGraph.size(); lVx++) {
			int gVx = match.get(lVx);	// matched x vertex in G
			for (int lVy = lVx + 1; lVy < lGraph.size(); lVy++) {
				int gVy = match.get(lVy);	// matched y vertex in G
				boolean edgeInG = targetGraph.hasEdge(gVx, gVy);
				boolean edgeInI = iGraph.hasEdge(gVx, gVy);
				if (edgeInG && !edgeInI) {
					// delete edge in G
				} else {
					// ??
				}
			}
			
		}
		return cGraph;
	}
	
	private Graph makeH(Graph cGraph) {
		// Use phiR to bridge cGraph to rGraph
		
		// TODO Auto-generated method stub
		return null;
	}

}
