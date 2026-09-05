package tailor.engine.rewrite;

import java.util.List;
import java.util.Optional;

/**
 * Rewrites a graph, by applying a set of rules that transform the graph. 
 */
public class Rewriter {
	
	private List<Rule> rules;
	
	private RuleApplicator ruleApplicator;
	
	public Rewriter(List<Rule> rules) {
		this.rules = rules;
		this.ruleApplicator = new RuleApplicator();
	}

	public Graph rewrite(Graph input) {
		Graph current = input;
		RuleMatch bestMatch;
		do {
			bestMatch = findBestMatch(current);
			if (bestMatch != null) {
				// TODO - store intermediate results?
				current = this.ruleApplicator.apply(
						bestMatch.rule(), bestMatch.match, current);
			}
		} while (bestMatch != null);
				
		return current;
	}
	
	private record RuleMatch(Rule rule, Match match) {}
	
	private RuleMatch findBestMatch(Graph graph) {
		RuleMatch bestMatch = null;
		for (Rule rule : rules) {
			// apply rule to graph
			Optional<Match> match = matchRule(rule, graph);
			if (match.isEmpty()) {
				continue;
			}
			if (bestMatch == null ||
					bestMatch.match().size() < match.get().size()) {
				bestMatch = new RuleMatch(rule, match.get());
			}
		}

		return bestMatch;
	}

	private Optional<Match> matchRule(Rule rule, Graph graph) {
		return new VF2Matcher(rule.getLGraph(), graph).matchFirst();
	}
}