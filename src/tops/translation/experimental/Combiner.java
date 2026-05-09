package tops.translation.experimental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Combiner extends AbstractOperator {
	
	private static Logger logger = Logger.getLogger(Combiner.class.getName());
	
	private final List<Pipe> inputs;
	
	public Combiner(Pipe... inputs) {
		this.inputs = Arrays.asList(inputs);
	}
	
	public void setId(String id) {
		super.setId(id);
		this.inputs.stream().forEach(pipe -> pipe.registerSink(this));
	}

	@Override
	public void run() {
		List<List<Result>> sourceBuffers = new ArrayList<>();	// TODO - avoid caching
		for (Pipe source : inputs) {
			sourceBuffers.add(getAll(source));
		}
		
		int counter = 0;
		for (Result mergedResult : combine(sourceBuffers)) {
			getOutput().put(mergedResult);
			counter++;
		}
		logger.info("Combiner (" + getId() + ") output " + counter + " results");
		
	}
	
	private List<Result> combine(List<List<Result>> input) {
        List<Result> listout = new ArrayList<>();
        rloop(input, listout, new ArrayList<>(), 0);
        return listout;
    }
	
	private List<Result> getAll(Pipe source) {
		List<Result> resultList = new ArrayList<>();
		while (source.hasNext()) {
			resultList.add(source.getNext());
		}
		return resultList;
	}

	private void rloop(List<List<Result>> input, List<Result> output, List<Result> combinations, int index) {
		if (index < input.size()) {
			List<Result> nextList = input.get(index);
			for (Result item : nextList) {
				if (reject(item, combinations)) {
					logger.info("NO Adding " + item + " to " + combinations);
				} else {
					List<Result> newcombinations = new ArrayList<>(combinations);
					logger.info("Adding " + item + " to " + newcombinations);
					newcombinations.add(item);
					rloop(input, output, newcombinations, index + 1);
				}
			}
		} else {
			Result flattened = flatten(combinations);	// TODO - could also flatten as we go ...
			logger.info("Flattening " + combinations + " to " + flattened);
			output.add(flattened);
		}
	}

	 private boolean reject(Result candidate, List<Result> combination) {
		 if (!combination.isEmpty()) {
			 Result last = combination.get(combination.size() - 1);
			 if (last.greaterThanOrEqual(candidate)) {
				 return true;
			 }
		 }
		 return false;
	 }

	 private Result flatten(List<Result> combinations) {
		 Result mergedResult = combinations.get(0).copy();
		 for (int index = 1; index < combinations.size(); index++) {
			 mergedResult = mergedResult.merge(combinations.get(index));
		 }
		 return mergedResult;
	 }

}
