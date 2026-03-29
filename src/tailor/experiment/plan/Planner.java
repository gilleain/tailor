package tailor.experiment.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.api.TmpOperator;
import tailor.experiment.description.AtomDescription;
import tailor.experiment.description.ChainDescription;
import tailor.experiment.description.GroupDescription;
import tailor.experiment.operator.AtomListPipe;
import tailor.experiment.operator.AtomPipe;
import tailor.experiment.operator.CombineAtomLists;
import tailor.experiment.operator.CombineAtoms;
import tailor.experiment.operator.PrintAtomLists;
import tailor.experiment.operator.ScanAtomByLabel;
import tailor.experiment.operator.ScanAtomListsByLabel;
import tailor.structure.Atom;

/**
 * Converts a {@link ChainDescription} into a pipeline of {@link Operator}
 */
public class Planner {
	
	// TODO - move up the hierarchy, and return Plan object?
	public List<Operator> plan(ChainDescription chainDescription) {
		List<Operator> pipeline = new ArrayList<>();
		
		// Go through the group descriptions in the chain, making scanners
		Map<TmpOperator, GroupDescription> scannerMap = new HashMap<>();
		for (GroupDescription groupDescription : chainDescription.getGroupDescriptions()) {
			List<AtomDescription> atomDescriptions = groupDescription.getAtomDescriptions();
			if (atomDescriptions.size() == 1) {
				String label = atomDescriptions.get(0).getLabel();
				TmpOperator scanByLabel = new ScanAtomByLabel(label, null, null); // TODO - fix constructor?
				scannerMap.put(scanByLabel, groupDescription);
			} else if (atomDescriptions.size() > 1) {
				List<String> labels = atomDescriptions.stream().map(a -> a.getLabel()).toList();
				TmpOperator scanByLabels = new ScanAtomListsByLabel(labels, null, null); // TODO - fix constructor?
				scannerMap.put(scanByLabels, groupDescription);
			}
		}
		
		// Add output pipes to the scanners
		List<Source<Atom>> outputPipes = new ArrayList<>();
		List<Source<List<Atom>>> outputListPipes = new ArrayList<>();
		for (Entry<TmpOperator, GroupDescription> entry : scannerMap.entrySet()) {
			TmpOperator operator = entry.getKey();
			pipeline.add(operator);
			
			// TODO - pretty clear what the issue with types is here ...
			if (operator instanceof ScanAtomByLabel scanAtomByLabel) {
				AtomPipe outputPipe = new AtomPipe();
				scanAtomByLabel.setSink(outputPipe);
				outputPipes.add(outputPipe);
			} else if (operator instanceof ScanAtomListsByLabel scanAtomListsByLabel) {
				AtomListPipe outputPipe = new AtomListPipe();
				scanAtomListsByLabel.setSink(outputPipe);
				outputListPipes.add(outputPipe);
			}
		}
		
		// Combine the scanners
		// TODO this is now crazy ...
		Operator combiner = null;
		if (!outputPipes.isEmpty()) {
//			AtomListPipe combinedOutput = new AtomListPipe();
			PrintAtomLists finalOut = new PrintAtomLists();	// TODO
			combiner = new CombineAtoms(outputPipes, finalOut);
			pipeline.add(combiner);
		}
		
		if (!outputListPipes.isEmpty()) {
			// TODO - clearly a more general type needed
			Sink<List<List<Atom>>> combinedOutput = new Sink<List<List<Atom>>>() {
				public void put(List<List<Atom>> item) {
					System.out.println(item);
				}
			};
			combiner = new CombineAtomLists(outputListPipes, combinedOutput);
			pipeline.add(combiner);
		}
		
		return pipeline;
	}

}
