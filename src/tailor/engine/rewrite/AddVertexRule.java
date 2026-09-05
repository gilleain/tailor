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
	public Attributes operate(Attributes oldAttributes) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Graph getLGraph() {
		return lGraph;
	}


	@Override
	public Graph getIGraph() {
		return iGraph;
	}

}
