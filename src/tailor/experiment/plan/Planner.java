package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tailor.experiment.api.Operator;
import tailor.experiment.api.PipeableOperator;
import tailor.experiment.api.Source;
import tailor.experiment.condition.AtomDistanceCondition;
import tailor.experiment.condition.AtomMatcher;
import tailor.experiment.description.AtomDistanceDescription;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.operator.CombineResults;
import tailor.experiment.operator.FilterAtomResultByCondition;
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
		
		// TODO - more general
		List<AtomDistanceDescription> betweenResidueDescriptions = new ArrayList<>();
		Map<GroupDescription, AtomDistanceDescription> internalResidueDescriptions = new HashMap<>();
		for (AtomDistanceDescription atomSetDescription : chainDescription.getAtomSetDescriptions()) {
			// check to see if the sub-descriptions are in the same group
			if (atomSetDescription.getAtomDescriptionA().getGroupDescription()
					== atomSetDescription.getAtomDescriptionB().getGroupDescription()) {
				GroupDescription groupDescription = atomSetDescription.getAtomDescriptionA().getGroupDescription();
				internalResidueDescriptions.put(groupDescription, atomSetDescription);
			} else {
				betweenResidueDescriptions.add(atomSetDescription);
			}
		}
		
		// Add output pipes to the scanners
		List<Source<Result>> outputResultPipes = new ArrayList<>();
		for (Entry<PipeableOperator<Result, Result>, GroupDescription> entry : scannerMap.entrySet()) {
			PipeableOperator<Result, Result> scanner = entry.getKey();
			pipeline.add(scanner);
			
			ResultPipe pipe = new ResultPipe();
			scanner.setSink(pipe);
			
			GroupDescription groupDescription = entry.getValue();
			if (internalResidueDescriptions.containsKey(groupDescription)) {
				AtomDistanceDescription atomSetDescription = internalResidueDescriptions.get(groupDescription);
				AtomDistanceCondition atomCondition = new AtomDistanceCondition(atomSetDescription.getDistance());
				List<String> labels = List.of(
						atomSetDescription.getAtomDescriptionA().getAtomDescription().getLabel(),
						atomSetDescription.getAtomDescriptionB().getAtomDescription().getLabel());
				AtomMatcher atomMatcher = new AtomMatcher(labels);
				FilterAtomResultByCondition filter = new FilterAtomResultByCondition(atomCondition, atomMatcher);
				filter.setSource(pipe);
				ResultPipe filteredPipe = new ResultPipe();
				filter.setSink(filteredPipe);
				outputResultPipes.add(filteredPipe);
				pipeline.add(filter);
			}
			
			outputResultPipes.add(pipe);
		}
		
		// Combine the scanners, unless there is only one
		if (scannerMap.size() > 1) {
			Operator combiner = new CombineResults(outputResultPipes, new PrintResults());
			pipeline.add(combiner);
		} else {
			final Source<Result> output = outputResultPipes.get(0);
			// kind of dumb
			Operator printAdapter = new Operator() {

				@Override
				public void run() {
					while (output.hasNext()) {
						System.out.println(output.getNext());
					}
				}
			};
			pipeline.add(printAdapter);
		}
		
		// TODO - Add the betweenResidueDescriptions as filters on combiners
		
		return pipeline;
	}

}
