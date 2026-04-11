package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import tailor.experiment.api.AtomListDescription;
import tailor.experiment.api.Operator;
import tailor.experiment.api.PipeableOperator;
import tailor.experiment.api.Source;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.description.group.GroupSequenceDescription;
import tailor.experiment.operator.CombineResults;
import tailor.experiment.operator.FilterAtomResultByCondition;
import tailor.experiment.operator.FilterAtomResultByCondition.ConditionMatcher;
import tailor.experiment.operator.PrintAdapter;
import tailor.experiment.operator.ResultPipe;
import tailor.experiment.operator.ScanAtomResultByLabel;
import tailor.experiment.plan.GroupUnionFind.Component;

/**
 * Converts a {@link ChainDescription} into a pipeline of {@link Operator}
 */
public class Planner {
	
	public Plan plan(ChainDescription chainDescription) {
		Plan plan = new Plan();
		
		// Go through the group descriptions in the chain, making scanners
		Map<GroupDescription, PipeableOperator<Result, Result>> scannerMap = new HashMap<>();
		for (GroupDescription groupDescription : chainDescription.getGroupDescriptions()) {
			List<String> labels = groupDescription.getAtomDescriptions().stream().map(a -> a.getLabel()).toList();
			PipeableOperator<Result, Result> scanByLabel = new ScanAtomResultByLabel(labels);
			scannerMap.put(groupDescription, scanByLabel);
			// add to the list of starts
			plan.addStart(scanByLabel);
		}
		
		// Extract and categorise the atom list descriptions
		Map<AtomListDescription, List<GroupDescription>> outerGroupDescriptions = new HashMap<>();
		Map<GroupDescription, Set<AtomListDescription>> innerGroupDescriptions = new HashMap<>();
		
		// Extract inner descriptions defined in group descriptions
		for (GroupDescription groupDescription : chainDescription.getGroupDescriptions()) {
			for (AtomListDescription atomListDescription : groupDescription.getAtomListDescriptions()) {
				add(groupDescription, atomListDescription, innerGroupDescriptions);	
			}
		}
		
		// Extract and separate inner and outer atom list descriptions
		for (AtomListDescription atomListDescription : chainDescription.getAtomListDescriptions()) {
			// check to see if the sub-descriptions are in the same group
			if (atomListDescription.isForSameGroup()) {
				GroupDescription groupDescription = atomListDescription.getFirstGroupDescription();
				add(groupDescription, atomListDescription, innerGroupDescriptions);
			} else {
				outerGroupDescriptions.put(atomListDescription, atomListDescription.getGroupDescriptions());
			}
		}
		
		// Join the scanners with combiners
		Stack<ResultPipe> combinedOutputPipes = join(plan, chainDescription, innerGroupDescriptions, outerGroupDescriptions, scannerMap);
		
		// Reduce the output pipes by joining
		ResultPipe current = combinedOutputPipes.pop();
		while (!combinedOutputPipes.isEmpty()) {
			ResultPipe next = combinedOutputPipes.pop();
			ResultPipe output = new ResultPipe();
			CombineResults combiner = new CombineResults(List.of(current, next), output);
			plan.addOperator(combiner);
			current = output;
		}
		
		// Wrap the output in a print (for now)
		plan.addOperator(new PrintAdapter("*", current));
		return plan;
	}
	
	private void add(GroupDescription groupDescription, AtomListDescription atomListDescription, Map<GroupDescription, Set<AtomListDescription>> innerGroupDescriptions) {
		if (innerGroupDescriptions.containsKey(groupDescription)) {
			innerGroupDescriptions.get(groupDescription).add(atomListDescription);
		} else {
			Set<AtomListDescription> atomListDescriptions = new HashSet<>();
			innerGroupDescriptions.put(groupDescription, atomListDescriptions);
			atomListDescriptions.add(atomListDescription);
		}
	}
	
	private Stack<ResultPipe> join(
			Plan plan,
			ChainDescription chainDescription,
			Map<GroupDescription, Set<AtomListDescription>> innerGroupDescriptions,
			Map<AtomListDescription, List<GroupDescription>> outerGroupDescriptions, 
			Map<GroupDescription, PipeableOperator<Result, Result>> scannerMap) {
		
		for (GroupSequenceDescription groupSequenceDescription : chainDescription.getGroupSequenceDescriptions()) {
			
		}
		
		// Find connected components of the graph where vertices are atoms and edges 
		// (or hyperedges) are AtomListDescriptions - the scanners are then joined based on this
		List<AtomListDescription> outerAtomListDescriptions = new ArrayList<>(outerGroupDescriptions.keySet()); 
		GroupUnionFind groupUnionFind = new GroupUnionFind(chainDescription.getGroupDescriptions());
		groupUnionFind.union(outerAtomListDescriptions);
		
		// The scanner for each group in a component needs to be joined together
		Stack<ResultPipe> combinedOutputPipes = new Stack<>();
		for (Component component : groupUnionFind.getComponents2()) {
			List<ResultPipe> componentOutputResultPipes = new ArrayList<>();
			for (GroupDescription groupDescription : component.groupDescriptions()) {
				PipeableOperator<Result, Result> scanner = scannerMap.get(groupDescription);

				ResultPipe scannerOutput = new ResultPipe();
				scanner.setSink(scannerOutput);

				// for groups that have inner conditions, create a filter and connect to the scanner
				if (innerGroupDescriptions.containsKey(groupDescription)) {
					Set<AtomListDescription> atomListDescriptions = innerGroupDescriptions.get(groupDescription);
					ResultPipe filterOutput = addInnerFilter(scannerOutput, atomListDescriptions, plan);
					componentOutputResultPipes.add(filterOutput);
				} else {
					componentOutputResultPipes.add(scannerOutput);
				}
			}
			// join these if there are more than one
			if (componentOutputResultPipes.size() > 1) {
				ResultPipe combinedOutput = new ResultPipe();
				CombineResults combiner = new CombineResults(componentOutputResultPipes, combinedOutput);
				plan.addOperator(combiner);
				
				Set<AtomListDescription> atomListDescriptions = component.atomListDescriptions();
				if (!component.atomListDescriptions().isEmpty()) {	// trivially, this should always be true
					ResultPipe filterOutput = addOuterFilter(combinedOutput, atomListDescriptions, plan);
					combinedOutputPipes.add(filterOutput);
				} else {
					combinedOutputPipes.add(combinedOutput);
				}
			} else {
				combinedOutputPipes.add((ResultPipe) componentOutputResultPipes.get(0));
			}
		}
		
		return combinedOutputPipes;
	}
	
	// TODO - merge these inner/outer methods
	private ResultPipe addOuterFilter(ResultPipe previousOutput, Set<AtomListDescription> atomListDescriptions, Plan plan) {
		FilterAtomResultByCondition filter = createMultiFilter(atomListDescriptions);
		filter.setSource(previousOutput);
		Source<Result> filterOutput = filter.getSinkAsSource();
		plan.addOperator(filter);
		return (ResultPipe) filterOutput;
	}
	
	private FilterAtomResultByCondition createMultiFilter(Set<AtomListDescription> atomListDescriptions) {
		List<ConditionMatcher> conditionMatchers = new ArrayList<>();
		for (AtomListDescription atomListDescription : atomListDescriptions) {
			conditionMatchers.add(new ConditionMatcher(atomListDescription.createCondition(), atomListDescription.createMatcher()));
		}
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(conditionMatchers);
		
		ResultPipe filteredPipe = new ResultPipe();
		filter.setSink(filteredPipe);
		return filter;
	}

	/**
	 * Add an inner filter : a filter between a subset of the atoms in a group.
	 * 
	 * @param previousOutput the input for the filter, which is the output from the previous step
	 * @param atomListDescriptions all condition descriptions to add
	 * @param pipeline the pipeline so far
	 * @return the output pipe from the filter
	 */
	private ResultPipe addInnerFilter(ResultPipe previousOutput, Set<AtomListDescription> atomListDescriptions, Plan plan) {
		FilterAtomResultByCondition filter = createMultiFilter(atomListDescriptions);
		filter.setSource(previousOutput);
		Source<Result> filterOutput = filter.getSinkAsSource();
		plan.addOperator(filter);
		return (ResultPipe) filterOutput;
	}
}
