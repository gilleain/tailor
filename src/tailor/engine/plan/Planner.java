package tailor.engine.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import tailor.api.AtomListDescription;
import tailor.api.Operator;
import tailor.condition.PropertyEquals;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;
import tailor.description.group.GroupNameDescription;
import tailor.description.group.GroupSequenceDescription;
import tailor.engine.operator.CombineResults;
import tailor.engine.operator.CombineResults.PipeSeqConstraint;
import tailor.engine.operator.FilterAtomResultByCondition;
import tailor.engine.operator.FilterGroupByDescription;
import tailor.engine.operator.Measurer;
import tailor.engine.operator.Pipe;
import tailor.engine.operator.PrintAdapter;
import tailor.engine.operator.ScanAtomResultByLabel;
import tailor.engine.plan.GroupUnionFind.Component;
import tailor.measure.GroupNameMeasure;

/**
 * Converts a {@link ChainDescription} into a pipeline of {@link Operator}
 */
public class Planner {
	

	public Plan plan(ChainDescription chainDescription) {
		return plan(chainDescription, true);
	}
		
    public Plan plan(ChainDescription chainDescription, boolean withPrintOutput) {	// TODO	
		Plan plan = new Plan();
		
		// this is needed at the moment for getting label partitions matching correctly
		labelDescription(chainDescription);
		
		// Go through the group descriptions in the chain, making scanners/group filters
		Map<GroupDescription, Pipe> startPointMap = new HashMap<>();
		for (GroupDescription groupDescription : chainDescription.getGroupDescriptions()) {
			Pipe output = addGroupOperators(plan, groupDescription);
			startPointMap.put(groupDescription, output);
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
		Stack<Pipe> combinedOutputPipes = 
				join(plan, chainDescription, innerGroupDescriptions, outerGroupDescriptions, startPointMap);
		
		// Reduce the output pipes by joining
		Pipe current = combinedOutputPipes.pop();
		while (!combinedOutputPipes.isEmpty()) {
			Pipe next = combinedOutputPipes.pop();
			Pipe output = new Pipe();
			CombineResults combiner;
			if (current.getSourceId().compareTo(next.getSourceId()) < 0) {
				combiner = new CombineResults(List.of(current, next), output);
			} else {
				combiner = new CombineResults(List.of(next, current), output);
			}
			plan.addOperator(combiner);
			output.registerSource(combiner); // TODO
			current = output;
		}
		
		// Add measures, if any
		if (!chainDescription.getAtomListMeasures().isEmpty()) {
			Measurer measurer = new Measurer(chainDescription.getAtomListMeasures());
			current = plan.addOperatorReturnPipe(measurer, current);
		}
		
		// Wrap the output in a print (for now)
		if (withPrintOutput) {
			plan.addOperator(new PrintAdapter("*", current));
		} else {
			plan.setOutputPoint(current);
		}
		return plan;
	}
    
    // Create a filter by group name then atom scanner, or just an atom scanner
    private Pipe addGroupOperators(Plan plan, GroupDescription groupDescription) {
    	Optional<String> groupName = groupDescription.getName();
    	if (groupName.isPresent()) {
			FilterGroupByDescription filterGroupByDescription = new FilterGroupByDescription(
					new GroupNameDescription(
							new PropertyEquals(groupName.get()), new GroupNameMeasure()));
			Pipe groupFilterOut = plan.addStart(filterGroupByDescription);
			Operator scanByLabel = addAtomScanner(plan, groupDescription);
			return plan.addOperatorReturnPipe(scanByLabel, groupFilterOut);
		} else {
			Operator scanByLabel = addAtomScanner(plan, groupDescription);
			return plan.addStart(scanByLabel);
		}
    }
    
    private Operator addAtomScanner(Plan plan, GroupDescription groupDescription) {
    	List<String> labels = groupDescription.getAtomDescriptions().stream().map(a -> a.getLabel()).toList();
		return new ScanAtomResultByLabel(labels);
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
	
	private Stack<Pipe> join(
			Plan plan,
			ChainDescription chainDescription,
			Map<GroupDescription, Set<AtomListDescription>> innerGroupDescriptions,
			Map<AtomListDescription, List<GroupDescription>> outerGroupDescriptions, 
			Map<GroupDescription, Pipe> startPointMap) {
		
		
		// Find connected components of the graph where vertices are groups and edges 
		// (or hyperedges) are (groups associated with) AtomListDescriptions or GroupSequenceDescriptions 
		// - the scanners are then joined based on the components found
		List<AtomListDescription> outerAtomListDescriptions = new ArrayList<>(outerGroupDescriptions.keySet()); 
		GroupUnionFind groupUnionFind = new GroupUnionFind(chainDescription.getGroupDescriptions());
		groupUnionFind.union(outerAtomListDescriptions, chainDescription.getGroupSequenceDescriptions());
		
		// The scanner for each group in a component needs to be joined together
		Stack<Pipe> combinedOutputPipes = new Stack<>();
		for (Component component : groupUnionFind.getComponents()) {
			List<Pipe> componentOutputResultPipes = new ArrayList<>();
			Map<GroupDescription, Pipe> groupDescriptionToOutputPipeMap = new HashMap<>();
			for (GroupDescription groupDescription : component.groupDescriptions()) {
				Pipe groupOutput = handleComponentGroup(plan, groupDescription, startPointMap, innerGroupDescriptions);
				componentOutputResultPipes.add(groupOutput);
				groupDescriptionToOutputPipeMap.put(groupDescription, groupOutput);
			}
			// join these if there are more than one
			if (componentOutputResultPipes.size() > 1) {
				Pipe combinedOutput = new Pipe();
				CombineResults combiner = makeCombiner(groupDescriptionToOutputPipeMap, combinedOutput, component);
				plan.addOperator(combiner);
				combinedOutput.registerSource(combiner); // TODO
				
				Set<AtomListDescription> atomListDescriptions = component.atomListDescriptions();
				if (!component.atomListDescriptions().isEmpty()) {	// trivially, this should always be true
					Pipe filterOutput = addFilter(plan, combinedOutput, atomListDescriptions);
					combinedOutputPipes.add(filterOutput);
				} else {
					combinedOutputPipes.add(combinedOutput);
				}
			} else {
				combinedOutputPipes.add((Pipe) componentOutputResultPipes.get(0));
			}
		}
		
		return combinedOutputPipes;
	}
	
	private Pipe handleComponentGroup(
			Plan plan,GroupDescription groupDescription,
			Map<GroupDescription, Pipe> startPointMap,
			Map<GroupDescription, Set<AtomListDescription>> innerGroupDescriptions) {
		Pipe startPoint = startPointMap.get(groupDescription);

		// for groups that have inner conditions, create a filter and connect to the scanner
		if (innerGroupDescriptions.containsKey(groupDescription)) {
			Set<AtomListDescription> atomListDescriptions = innerGroupDescriptions.get(groupDescription);
			Pipe filterOutput = addFilter(plan, startPoint, atomListDescriptions);
			return filterOutput;
		} else {
			return startPoint;
		}
	}
	
	private CombineResults makeCombiner(Map<GroupDescription, Pipe> groupDescriptionToOutputPipeMap, Pipe combinedOutput, Component component) {
		List<CombineResults.PipeSeqConstraint> seqConstraints = new ArrayList<>();
		for (GroupSequenceDescription groupSequenceDescription : component.groupSequenceDescriptions()) {
			Pipe startPipe = groupDescriptionToOutputPipeMap.get(groupSequenceDescription.getStart());
			Pipe endPipe = groupDescriptionToOutputPipeMap.get(groupSequenceDescription.getEnd());
			seqConstraints.add(new PipeSeqConstraint(startPipe, endPipe, groupSequenceDescription));
		}
		List<Pipe> inputs = new ArrayList<>(groupDescriptionToOutputPipeMap.values());
		inputs.sort((a, b) -> a.getSourceId().compareTo(b.getSourceId())); // sort on source ids - bit hacky
		return new CombineResults(inputs, combinedOutput, seqConstraints);
	}
	
	/**
	 * Add a filter between a subset of the atoms in a group or set of groups
	 *
	 * @param plan the plan so far
	 * @param previousOutput the input for the filter, which is the output from the previous step
	 * @param atomListDescriptions all condition descriptions to add

	 * @return the output pipe from the filter
	 */
	private Pipe addFilter(Plan plan, Pipe previousOutput, Set<AtomListDescription> atomListDescriptions) {
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(new ArrayList<>(atomListDescriptions));
		return plan.addOperatorReturnPipe(filter, previousOutput);
	}
}
