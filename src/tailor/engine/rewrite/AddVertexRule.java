package tailor.engine.rewrite;

import java.util.Map;

/**
 * Simple example rule that matches an edge (L) and converts to a triangle (R) by adding a vertex and two edges.
 */
public class AddVertexRule implements Rule {
	
	// match as sub-graph in the target
	private final Graph lGraph = makeLGraph();

	private Graph makeLGraph() {
		Graph graph = new Graph();
		graph.addEdge(0, 1);	// single unlabelled edge
		return graph;
	}
	
	// Replaces the l-graph in the target
	private final Graph rGraph = makeRGraph();

	private Graph makeRGraph() {
		Graph graph = new Graph();
		graph.addEdge(0, 1);
		graph.addEdge(0, 2);
		graph.addEdge(1, 2);
		return graph;
	}
	
	private Graph iGraph = makeIGraph();
	
	private Graph makeIGraph() {
		Graph graph = new Graph();
		graph.addVertex();
		graph.addVertex();
		return graph;
	}

	
	@Override
	public Graph pattern() {
		return lGraph;
	}
	
	// this is the inverted morphism from I to L
	private Match inversePhiL = makeInversePhiL();
	
	private Match makeInversePhiL() {
		return new Match(Map.of(0, 0, 1, 1));
	}
	
	// the morphism from I to R
	private Match phiR = makePhiR();
	
	private Match makePhiR() {
		return new Match(Map.of(0, 0, 1, 1));
	}


	@Override
	public Graph apply(Match match, Graph targetGraph) {
		// Determine obsolete parts of G, to make C
		Graph cGraph = makeC(match, targetGraph);
		
		// Determine fresh parts to add to C to make H
		Graph hGraph = makeH(cGraph);
		
		return hGraph;
	}


	private Graph makeC(Match match, Graph targetGraph) {
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
