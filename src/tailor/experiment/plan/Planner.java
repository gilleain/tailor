package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import tailor.experiment.operator.PrintResults;
import tailor.experiment.operator.ResultPipe;
import tailor.experiment.operator.ScanAtomResultByLabel;

/**
 * Converts a {@link ChainDescription} into a pipeline of {@link Operator}
 */
public class Planner {
	
	// TODO - move up the hierarchy, and return Plan object?
	public List<Operator> plan(ChainDescription chainDescription) {
		List<Operator> pipeline = new ArrayList<>();
		
		// Go through the group descriptions in the chain, making scanners
		Map<PipeableOperator<Result, Result>, GroupDescription> scannerMap = new HashMap<>();
		for (GroupDescription groupDescription : chainDescription.getGroupDescriptions()) {
			List<String> labels = groupDescription.getAtomDescriptions().stream().map(a -> a.getLabel()).toList();
			PipeableOperator<Result, Result> scanByLabel = new ScanAtomResultByLabel(labels);
			scannerMap.put(scanByLabel, groupDescription);
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
		pipeline.add(join(pipeline, innerGroupDescriptions, outerGroupDescriptions, scannerMap));
		
		// TODO - Add the betweenResidueDescriptions as filters on combiners
		
		return pipeline;
	}
	
	private Operator join(
			List<Operator> pipeline, 
			Map<GroupDescription, AtomListDescription> innerGroupDescriptions,
			List<AtomListDescription> outerGroupDescriptions, 
			Map<PipeableOperator<Result, Result>, GroupDescription> scannerMap) {
		
		List<Source<Result>> outputResultPipes = new ArrayList<>();
		for (Entry<PipeableOperator<Result, Result>, GroupDescription> entry : scannerMap.entrySet()) {
			PipeableOperator<Result, Result> scanner = entry.getKey();
			pipeline.add(scanner);
			
			ResultPipe scannerOutput = new ResultPipe();
			scanner.setSink(scannerOutput);
			
			// for groups that have inner conditions, create a filter
			GroupDescription groupDescription = entry.getValue();
			if (innerGroupDescriptions.containsKey(groupDescription)) {
				AtomListDescription atomSetDescription = innerGroupDescriptions.get(groupDescription);
				FilterAtomResultByCondition filter = addFilter(outputResultPipes, groupDescription, atomSetDescription);
				filter.setSource(scannerOutput);
				pipeline.add(filter);
			}
			
			outputResultPipes.add(scannerOutput);
		}
		if (outputResultPipes.size() > 1) {
			return new CombineResults(outputResultPipes, new PrintResults());
		} else {
			return new PrintAdapter(outputResultPipes.get(0));
		}
	}
	
	private FilterAtomResultByCondition addFilter(
			List<Source<Result>> outputResultPipes, 
			GroupDescription groupDescription,
			AtomListDescription atomListDescription) {
		AtomListCondition atomCondition = atomListDescription.makeCondition();
		AtomMatcher atomMatcher = atomListDescription.createMatcher();
		FilterAtomResultByCondition filter = new FilterAtomResultByCondition(atomCondition, atomMatcher);
		ResultPipe filteredPipe = new ResultPipe();
		filter.setSink(filteredPipe);
		outputResultPipes.add(filteredPipe);
		return filter;
	}

}
