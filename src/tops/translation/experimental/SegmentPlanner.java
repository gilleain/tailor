package tops.translation.experimental;

import java.util.Stack;

import tailor.api.SegmentListDescription;
import tailor.description.ChainDescription;
import tailor.engine.operator.Pipe;

public class SegmentPlanner {
	
	public Plan makePlan(ChainDescription chainDescription) {
		Plan plan = new Plan();
		
		// Convert segment descriptions to type filters
		Stack<Pipe> outputs = new Stack<>();
		for (SegmentDescription segment : chainDescription.getSegments()) {
			Pipe input = new Pipe();
			Pipe output = plan.addInputOperator(new SegmentTypeFilter(segment.getType(), input), input);
			if (!segment.getPropertyDescriptions().isEmpty()) {
				output = addInnerFilter(plan, output, segment);
			}
			outputs.add(output);
		}
		
		// Combine these
		Pipe currentOutput = outputs.pop();	// TODO - reverse order?
		while (!outputs.empty()) {
			Pipe nextOutput = outputs.pop();
			Pipe combinedOutput = plan.addOperator(new Combiner(nextOutput, currentOutput));	// TODO - hack reversing here
			currentOutput = combinedOutput;
		}
		
		// TODO - need to work out where to put these filters ...
		for (SegmentListDescription segmentListDescription : chainDescription.getSegmentListDescription()) {
			FilterSegmentByListDescription filter = new FilterSegmentByListDescription(currentOutput, segmentListDescription);
			currentOutput = plan.addOperator(filter);
		}
		
		plan.setOutputPipe(currentOutput);
		
		return plan;
	}
	
	private Pipe addInnerFilter(Plan plan, Pipe input, SegmentDescription segmentDescription) {
		return plan.addOperator(
				new FilterSegmentByPropertyDescription(input, segmentDescription.getPropertyDescriptions()));
	}

}
