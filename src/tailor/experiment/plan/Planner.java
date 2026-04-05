package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import tailor.experiment.api.AtomListCondition;
import tailor.experiment.api.AtomListDescription;
import tailor.experiment.api.Operator;
import tailor.experiment.api.PipeableOperator;
import tailor.experiment.api.Source;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.operator.CombineResults;
import tailor.experiment.operator.FilterAtomResultByCondition;
import tailor.experiment.operator.PrintAdapter;
import tailor.experiment.operator.ResultPipe;
import tailor.experiment.operator.ScanAtomResultByLabel;

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
			scanByLabel.setId(String.valueOf(operatorId++));
			scannerMap.put(groupDescription, scanByLabel);
		}
		
		// Extract and categorise the atom list descriptions
		List<AtomListDescription> outerGroupDescriptions = new ArrayList<>();
		Map<GroupDescription, AtomListDescription> innerGroupDescriptions = new HashMap<>();
		for (AtomListDescription atomSetDescription : chainDescription.getAtomListDescriptions()) {
			// check to see if the sub-descriptions are in the same group
			if (atomSetDescription.isForSameGroup()) {
				GroupDescription groupDescription = atomSetDescription.getFirstGroupDescription();
				innerGroupDescriptions.put(groupDescription, atomSetDescription);
			} else {
				outerGroupDescriptions.add(atomSetDescription);
			}
		}
		
		// Join the scanners with combiners
		pipeline.add(join(pipeline, chainDescription, innerGroupDescriptions, outerGroupDescriptions, scannerMap));
		
		return pipeline;
	}
	
	private Operator join(
			List<Operator> pipeline, 
			ChainDescription chainDescription,
			Map<GroupDescription, AtomListDescription> innerGroupDescriptions,
			List<AtomListDescription> outerGroupDescriptions, 
			Map<GroupDescription, PipeableOperator<Result, Result>> scannerMap) {
		
		// Find connected components of the graph where vertices are atoms and edges 
		// (or hyperedges) are AtomListDescriptions - the scanners are then joined based on this
		List<GroupDescription> allGroups = chainDescription.getGroupDescriptions();
		GroupUnionFind groupUnionFind = new GroupUnionFind(allGroups);
		groupUnionFind.union(outerGroupDescriptions);
		List<List<GroupDescription>> components = groupUnionFind.getComponents();
		
		// The scanner for each group in a component needs to be joined together
		Stack<ResultPipe> combinedOutputPipes = new Stack<>();
		List<GroupDescription> singletons = new ArrayList<>();	// components of size 1
		for (List<GroupDescription> component : components) {
//			if (component.size() == 1) {
//				singletons.add(component.get(0));
//			} else {
				List<ResultPipe> componentOutputResultPipes = new ArrayList<>();
				for (GroupDescription groupDescription : component) {
					PipeableOperator<Result, Result> scanner = scannerMap.get(groupDescription);
					pipeline.add(scanner);
					
					ResultPipe scannerOutput = new ResultPipe();
					scanner.setSink(scannerOutput);
					
					// for groups that have inner conditions, create a filter and connect to the scanner
					if (innerGroupDescriptions.containsKey(groupDescription)) {
						AtomListDescription atomSetDescription = innerGroupDescriptions.get(groupDescription);
						ResultPipe filterOutput = addFilter(scannerOutput, atomSetDescription, pipeline);
						componentOutputResultPipes.add(filterOutput);
					} else {
						componentOutputResultPipes.add(scannerOutput);
					}
				}
				// join these if there are more than one
				if (componentOutputResultPipes.size() > 1) {
					ResultPipe combinedOutput = new ResultPipe();
					CombineResults combiner = new CombineResults(String.valueOf(operatorId++), componentOutputResultPipes, combinedOutput);
					combinedOutputPipes.add(combinedOutput);
					pipeline.add(combiner);
				} else {
					combinedOutputPipes.add((ResultPipe) componentOutputResultPipes.get(0));
				}
//			}
		}
		
		// The singleton components are now joined to these larger joined components
		for (GroupDescription singleton : singletons) {
			
		}
		
		// Reduce the output pipes by joining
		ResultPipe current = combinedOutputPipes.pop();
		while (!combinedOutputPipes.isEmpty()) {
			ResultPipe next = combinedOutputPipes.pop();
			ResultPipe output = new ResultPipe();
			CombineResults combiner = new CombineResults(String.valueOf(operatorId++), List.of(current, next), output);
			pipeline.add(combiner);
			current = output;
		}
		
		// TODO - Add the betweenResidueDescriptions as filters on combiners
		
		return new PrintAdapter(String.valueOf(operatorId++), current);
	}
	
	private ResultPipe addFilter(ResultPipe scannerOutput, AtomListDescription atomSetDescription, List<Operator> pipeline) {
		FilterAtomResultByCondition filter = createFilter(atomSetDescription);
		filter.setSource(scannerOutput);
		Source<Result> filterOutput = filter.getSinkAsSource();
		pipeline.add(filter);
		return (ResultPipe) filterOutput;
	}
	
	private FilterAtomResultByCondition createFilter(AtomListDescription atomListDescription) {
		AtomListCondition atomCondition = atomListDescription.makeCondition();
		AtomMatcher atomMatcher = atomListDescription.createMatcher();
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(atomCondition, atomMatcher);
		filter.setId(String.valueOf(operatorId++));
		ResultPipe filteredPipe = new ResultPipe();
		filter.setSink(filteredPipe);
		return filter;
	}
	
}
