package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tailor.experiment.api.Operator;
import tailor.experiment.api.PipeableOperator;
import tailor.experiment.api.Source;
import tailor.experiment.description.AtomDescription;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.operator.CombineResults;
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
			List<AtomDescription> atomDescriptions = groupDescription.getAtomDescriptions();
			List<String> labels = atomDescriptions.stream().map(a -> a.getLabel()).toList();
			PipeableOperator<Result, Result> scanByLabel = new ScanAtomResultByLabel(labels);
			scannerMap.put(scanByLabel, groupDescription);
		}
		
		// Add output pipes to the scanners
		List<Source<Result>> outputResultPipes = new ArrayList<>();
		for (Entry<PipeableOperator<Result, Result>, GroupDescription> entry : scannerMap.entrySet()) {
			PipeableOperator<Result, Result> operator = entry.getKey();
			pipeline.add(operator);
			ResultPipe pipe = new ResultPipe();
			operator.setSink(pipe);
			outputResultPipes.add(pipe);
		}
		
		// Combine the scanners
		Operator combiner = new CombineResults(outputResultPipes, new PrintResults());
		pipeline.add(combiner);
		
		return pipeline;
	}

}
