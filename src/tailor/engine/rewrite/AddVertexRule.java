package tailor.engine.rewrite;

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

	
	@Override
	public Graph pattern() {
		return lGraph;
	}

	@Override
	public Graph apply(Match match, Graph targetGraph) {
		// use the match to convert 
		
		// TODO Auto-generated method stub
		return null;
	}

}
