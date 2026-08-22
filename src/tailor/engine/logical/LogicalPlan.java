package tailor.engine.logical;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogicalPlan {
	
	private List<LogicalOperator> operators;
	
	/*
	 * The inputs to the flow, for example chain filters
	 */
	private List<LogicalOperator> roots;
	
	public record Edge(LogicalOperator from, LogicalOperator to) {
		public String toString() {
			return from.getClass().getSimpleName() + "->" + to.getClass().getSimpleName();
		}
	}
	
	private List<Edge> edges;
	
	public LogicalPlan() {
		this.operators = new ArrayList<>();
		this.edges = new ArrayList<>();
	}
	
	public void addRoot(LogicalOperator logicalOperator) {
		this.roots.add(logicalOperator);
		addOperator(logicalOperator);
	}
	
	public List<LogicalOperator> getRoots() {
		return this.roots;
	}
	
	
	public void addOperator(LogicalOperator logicalOperator) {
		this.operators.add(logicalOperator);
	}
	
	public void addOperatorFrom(LogicalOperator fromOperator, LogicalOperator toOperator) {
		addOperator(toOperator);
		this.edges.add(new Edge(fromOperator, toOperator));
	}
	
	public String toString() {
		String operatorString = 
				operators.stream().map(Object::toString).collect(Collectors.joining("\n"));
		return operatorString + "\n" + edges.stream().map(Edge::toString).collect(Collectors.joining("\n"));
	}

	/**
	 * List all the nodes where there is an edge from the current to this next node.
	 * 
	 * @param current
	 * @return
	 */
	public List<LogicalOperator> getNext(LogicalOperator current) {
		List<LogicalOperator> next = new ArrayList<>();
		for (Edge edge : this.edges) {
			if (edge.from == current) {
				next.add(edge.to);
			}
		}
		return next;
	}

}
