package tailor.engine.rewrite;

import java.util.List;

/**
 * Rewrites a graph, by applying a set of rules that transform the graph. 
 */
public class Rewriter {
	
	private List<Rule> rules;
	
	public Rewriter(List<Rule> rules) {
		this.rules = rules;
	}

	public Graph rewrite(Graph input) {
		Graph current = input;
		RuleMatch bestMatch;
		do {
			bestMatch = findBestMatch(current);
			if (bestMatch != null) {
				bestMatch.rule().apply(bestMatch.match(), current);
			}
		} while (bestMatch != null);
				
		return current;
	}
	
	private record RuleMatch(Rule rule, Match match) {}
	
	private RuleMatch findBestMatch(Graph graph) {
		RuleMatch bestMatch = null;
		for (Rule rule : rules) {
			// apply rule to graph
			Match match = matchRule(rule, graph);
			if (match == null) {
				continue;
			}
			if (bestMatch == null ||
					bestMatch.match().size() < match.size()) {
				bestMatch = new RuleMatch(rule, match);
			}
		}

		return bestMatch;
	}

	private Match matchRule(Rule rule, Graph graph) {
		return new VF2Matcher(rule.pattern(), graph).matchFirst().orElse(null);
	}
}