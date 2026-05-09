package tops.translation.experimental;

import java.util.Stack;

public class Planner {
	
	public Plan makePlan(ChainDescription chainDescription) {
		Plan plan = new Plan();
		
		// Convert segment descriptions to type filters
		Stack<Pipe> outputs = new Stack<>();
		for (SegmentDescription segment : chainDescription.getSegments()) {
			Pipe input = new Pipe();
			Pipe output = plan.addInputOperator(new SegmentTypeFilter(segment.getType(), input), input);
			outputs.add(output);
		}
		
		// Combine these
		Pipe currentOutput = outputs.pop();	// TODO - reverse order?
		while (!outputs.empty()) {
			Pipe nextOutput = outputs.pop();
			Pipe combinedOutput = plan.addOperator(new Combiner(nextOutput, currentOutput));	// TODO - hack reversing here
			currentOutput = combinedOutput;
		}
		
		plan.setOutputPipe(currentOutput);
		
		return plan;
	}

}
