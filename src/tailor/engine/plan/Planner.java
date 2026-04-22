package tailor.engine.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import tailor.api.AtomListCondition;
import tailor.api.AtomListDescription;
import tailor.api.Operator;
import tailor.api.PipeableOperator;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;
import tailor.description.group.GroupSequenceDescription;
import tailor.engine.operator.CombineResults;
import tailor.engine.operator.CombineResults.PipeSeqConstraint;
import tailor.engine.plan.GroupUnionFind.Component;
import tailor.engine.operator.FilterAtomResultByCondition;
import tailor.engine.operator.Measurer;
import tailor.engine.operator.PrintAdapter;
import tailor.engine.operator.ResultPipe;
import tailor.engine.operator.ScanAtomResultByLabel;

/**
 * Converts a {@link ChainDescription} into a pipeline of {@link Operator}
 */
public class Planner {
	
	public Plan plan(ChainDescription chainDescription) {
		Plan plan = new Plan();
		
		// this is needed at the moment for getting label partitions matching correctly
		labelDescription(chainDescription);
		
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
				addToInnerListDescriptions(groupDescription, atomListDescription, innerGroupDescriptions);	
			}
		}
		
		// Extract and separate inner and outer atom list descriptions
		for (AtomListDescription atomListDescription : chainDescription.getAtomListDescriptions()) {
			// check to see if the sub-descriptions are in the same group
			List<GroupDescription> groupDescriptions = atomListDescription.getGroupDescriptions();
			GroupDescription firstGroupDescription = groupDescriptions.get(0);
			boolean isForSameGroup = groupDescriptions.stream().allMatch(g -> g == firstGroupDescription);
			if (isForSameGroup) {
				addToInnerListDescriptions(firstGroupDescription, atomListDescription, innerGroupDescriptions);
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
		
		// Add measures, if any
		if (!chainDescription.getAtomListMeasures().isEmpty()) {
			Measurer measurer = new Measurer(chainDescription.getAtomListMeasures());
			plan.addOperator(measurer);
			measurer.setSource(current);
			current = new ResultPipe();
			measurer.setSink(current);
		}
		
		// Wrap the output in a print (for now)
		plan.addOperator(new PrintAdapter("*", current));
		return plan;
	}
	
	private void labelDescription(ChainDescription chainDescription) {	// TODO - expand on this?
		int index = 0;
		for (GroupDescription groupDescription : chainDescription.getGroupDescriptions()) {
			groupDescription.setIndex(index);
			index++;
		}
	}
	
	private void addToInnerListDescriptions(
			GroupDescription groupDescription, AtomListDescription atomListDescription, 
			Map<GroupDescription, Set<AtomListDescription>> innerGroupDescriptions) {
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
		
		
		// Find connected components of the graph where vertices are groups and edges 
		// (or hyperedges) are (groups associated with) AtomListDescriptions or GroupSequenceDescriptions 
		// - the scanners are then joined based on the components found
		List<AtomListDescription> outerAtomListDescriptions = new ArrayList<>(outerGroupDescriptions.keySet()); 
		GroupUnionFind groupUnionFind = new GroupUnionFind(chainDescription.getGroupDescriptions());
		groupUnionFind.union(outerAtomListDescriptions, chainDescription.getGroupSequenceDescriptions());
		
		// The scanner for each group in a component needs to be joined together
		Stack<ResultPipe> combinedOutputPipes = new Stack<>();
		for (Component component : groupUnionFind.getComponents()) {
			List<ResultPipe> componentOutputResultPipes = new ArrayList<>();
			Map<GroupDescription, ResultPipe> groupDescriptionToOutputPipeMap = new HashMap<>();
			for (GroupDescription groupDescription : component.groupDescriptions()) {
				ResultPipe groupOutput = handleComponentGroup(plan, groupDescription, scannerMap, innerGroupDescriptions);
				componentOutputResultPipes.add(groupOutput);
				groupDescriptionToOutputPipeMap.put(groupDescription, groupOutput);
			}
			// join these if there are more than one
			if (componentOutputResultPipes.size() > 1) {
				ResultPipe combinedOutput = new ResultPipe();
				plan.addOperator(makeCombiner(groupDescriptionToOutputPipeMap, combinedOutput, component));
				
				Set<AtomListDescription> atomListDescriptions = component.atomListDescriptions();
				if (!component.atomListDescriptions().isEmpty()) {	// trivially, this should always be true
					ResultPipe filterOutput = addFilter(combinedOutput, atomListDescriptions, plan);
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
	
	private ResultPipe handleComponentGroup(
			Plan plan,GroupDescription groupDescription,
			Map<GroupDescription, PipeableOperator<Result, Result>> scannerMap,
			Map<GroupDescription, Set<AtomListDescription>> innerGroupDescriptions) {
		PipeableOperator<Result, Result> scanner = scannerMap.get(groupDescription);

		ResultPipe scannerOutput = new ResultPipe();
		scanner.setSink(scannerOutput);

		// for groups that have inner conditions, create a filter and connect to the scanner
		if (innerGroupDescriptions.containsKey(groupDescription)) {
			Set<AtomListDescription> atomListDescriptions = innerGroupDescriptions.get(groupDescription);
			ResultPipe filterOutput = addFilter(scannerOutput, atomListDescriptions, plan);
			return filterOutput;
		} else {
			return scannerOutput;
		}
	}
	
	private CombineResults makeCombiner(Map<GroupDescription, ResultPipe> groupDescriptionToOutputPipeMap, ResultPipe combinedOutput, Component component) {
		List<CombineResults.PipeSeqConstraint> seqConstraints = new ArrayList<>();
		for (GroupSequenceDescription groupSequenceDescription : component.groupSequenceDescriptions()) {
			ResultPipe startPipe = groupDescriptionToOutputPipeMap.get(groupSequenceDescription.getStart());
			ResultPipe endPipe = groupDescriptionToOutputPipeMap.get(groupSequenceDescription.getEnd());
			seqConstraints.add(new PipeSeqConstraint(startPipe, endPipe, groupSequenceDescription));
		}
		List<ResultPipe> inputs = new ArrayList<>(groupDescriptionToOutputPipeMap.values());
		inputs.sort((a, b) -> a.getSourceId().compareTo(b.getSourceId())); // sort on source ids - bit hacky
		return new CombineResults(inputs, combinedOutput, seqConstraints);
	}
	
	/**
	 * Add a filter between a subset of the atoms in a group or set of groups
	 * 
	 * @param previousOutput the input for the filter, which is the output from the previous step
	 * @param atomListDescriptions all condition descriptions to add
	 * @param pipeline the pipeline so far
	 * @return the output pipe from the filter
	 */
	private ResultPipe addFilter(ResultPipe previousOutput, Set<AtomListDescription> atomListDescriptions, Plan plan) {
		List<AtomListCondition> conditions = 
				atomListDescriptions.stream().map(AtomListDescription::createCondition).toList();
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(conditions);
		
		ResultPipe filterOutput = new ResultPipe();
		filter.setSink(filterOutput);
		
		// TODO - nasty hidden effect here where we have to call this before setSource to ensure ids
		// are set correctly!
		plan.addOperator(filter);
		filter.setSource(previousOutput);
		
		return filterOutput;
	}
}
