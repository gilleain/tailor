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
	
	private int operatorId;	// TODO - move to Plan class?
	
	// TODO - move up the hierarchy, and return Plan object?
	public List<Operator> plan(ChainDescription chainDescription) {
		operatorId = 1;
		List<Operator> pipeline = new ArrayList<>();
		
		// Go through the group descriptions in the chain, making scanners
		Map<GroupDescription, PipeableOperator<Result, Result>> scannerMap = new HashMap<>();
		for (GroupDescription groupDescription : chainDescription.getGroupDescriptions()) {
			List<String> labels = groupDescription.getAtomDescriptions().stream().map(a -> a.getLabel()).toList();
			PipeableOperator<Result, Result> scanByLabel = new ScanAtomResultByLabel(labels);
			scanByLabel.setId(getOperatorId());
			scannerMap.put(groupDescription, scanByLabel);
		}
		
		// Extract and categorise the atom list descriptions
		Map<AtomListDescription, List<GroupDescription>> outerGroupDescriptions = new HashMap<>();
		Map<GroupDescription, Set<AtomListDescription>> innerGroupDescriptions = new HashMap<>();
		for (AtomListDescription atomListDescription : chainDescription.getAtomListDescriptions()) {
			// check to see if the sub-descriptions are in the same group
			if (atomListDescription.isForSameGroup()) {
				GroupDescription groupDescription = atomListDescription.getFirstGroupDescription();
				// TODO - this should be a LIST of atomSetDescriptions, in case there are multiple!
				if (innerGroupDescriptions.containsKey(groupDescription)) {
					innerGroupDescriptions.get(groupDescription).add(atomListDescription);
				} else {
					Set<AtomListDescription> atomListDescriptions = new HashSet<>();
					innerGroupDescriptions.put(groupDescription, atomListDescriptions);
					atomListDescriptions.add(atomListDescription);
				}
			} else {
				outerGroupDescriptions.put(atomListDescription, atomListDescription.getGroupDescriptions());
			}
		}
		
		// Join the scanners with combiners
		pipeline.add(join(pipeline, chainDescription, innerGroupDescriptions, outerGroupDescriptions, scannerMap));
		
		return pipeline;
	}
	
	private Operator join(
			List<Operator> pipeline, 
			ChainDescription chainDescription,
			Map<GroupDescription, Set<AtomListDescription>> innerGroupDescriptions,
			Map<AtomListDescription, List<GroupDescription>> outerGroupDescriptions, 
			Map<GroupDescription, PipeableOperator<Result, Result>> scannerMap) {
		
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
				pipeline.add(scanner);

				ResultPipe scannerOutput = new ResultPipe();
				scanner.setSink(scannerOutput);

				// for groups that have inner conditions, create a filter and connect to the scanner
				if (innerGroupDescriptions.containsKey(groupDescription)) {
					Set<AtomListDescription> atomListDescriptions = innerGroupDescriptions.get(groupDescription);
					ResultPipe filterOutput = addInnerFilter(scannerOutput, atomListDescriptions, pipeline);
					componentOutputResultPipes.add(filterOutput);
				} else {
					componentOutputResultPipes.add(scannerOutput);
				}
			}
			// join these if there are more than one
			if (componentOutputResultPipes.size() > 1) {
				ResultPipe combinedOutput = new ResultPipe();
				CombineResults combiner = new CombineResults(getOperatorId(), componentOutputResultPipes, combinedOutput);
				pipeline.add(combiner);
				
				Set<AtomListDescription> atomListDescriptions = component.atomListDescriptions();
				if (!component.atomListDescriptions().isEmpty()) {	// trivially, this should always be true
					ResultPipe filterOutput = addOuterFilter(combinedOutput, atomListDescriptions, pipeline);
					combinedOutputPipes.add(filterOutput);
				} else {
					combinedOutputPipes.add(combinedOutput);
				}
			} else {
				combinedOutputPipes.add((ResultPipe) componentOutputResultPipes.get(0));
			}
		}
		
		// Reduce the output pipes by joining
		ResultPipe current = combinedOutputPipes.pop();
		while (!combinedOutputPipes.isEmpty()) {
			ResultPipe next = combinedOutputPipes.pop();
			ResultPipe output = new ResultPipe();
			CombineResults combiner = new CombineResults(getOperatorId(), List.of(current, next), output);
			pipeline.add(combiner);
			current = output;
		}
		
		// TODO - Add the betweenResidueDescriptions as filters on combiners
		
		return new PrintAdapter(getOperatorId(), current);
	}
	
	// TODO - merge these inner/outer methods
	private ResultPipe addOuterFilter(ResultPipe previousOutput, Set<AtomListDescription> atomListDescriptions, List<Operator> pipeline) {
		FilterAtomResultByCondition filter = createMultiFilter(atomListDescriptions);
		filter.setSource(previousOutput);
		Source<Result> filterOutput = filter.getSinkAsSource();
		pipeline.add(filter);
		return (ResultPipe) filterOutput;
	}
	
	private FilterAtomResultByCondition createMultiFilter(Set<AtomListDescription> atomListDescriptions) {
		List<ConditionMatcher> conditionMatchers = new ArrayList<>();
		for (AtomListDescription atomListDescription : atomListDescriptions) {
			conditionMatchers.add(new ConditionMatcher(atomListDescription.makeCondition(), atomListDescription.createMatcher()));
		}
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(conditionMatchers);
		filter.setId(getOperatorId());
		
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
	private ResultPipe addInnerFilter(ResultPipe previousOutput, Set<AtomListDescription> atomListDescriptions, List<Operator> pipeline) {
		FilterAtomResultByCondition filter = createMultiFilter(atomListDescriptions);
		filter.setSource(previousOutput);
		Source<Result> filterOutput = filter.getSinkAsSource();
		pipeline.add(filter);
		return (ResultPipe) filterOutput;
	}
	
	
	private String getOperatorId() {
		String operatorIdString = String.valueOf(operatorId);
		operatorId++;
		return operatorIdString;
	}
	
}
