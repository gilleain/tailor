package tailor.engine.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tailor.api.Sink;
import tailor.api.Source;
import tailor.description.group.GroupSequenceDescription;
import tailor.engine.plan.Result;

/**
 * Combine the output from multiple sources, flattening them into a list of results.
 */
public class CombineResults extends AbstractOperator {
	
	private static Logger logger = Logger.getLogger(CombineResults.class.getName());
	
	private List<ResultPipe> sources;
	
	private Sink<Result> output;
	
	/**
	 * Mapping between the end point index of an input source, and the separation (gap) 
	 */
	private Map<Integer, Integer> gapMap;
	
	// Holds the association between two input pipes, and a sequence constraint
	// TODO - could consider just using the end ...
	public record PipeSeqConstraint(ResultPipe start, ResultPipe end, GroupSequenceDescription groupSeqDescription) {}
	
	
	// TODO - tidy constructors
	public CombineResults(List<ResultPipe> sources, ResultPipe output) {
		this.sources = sources;
		this.output = output;
		output.registerSource(this);
		this.gapMap = new HashMap<>();
	}
	
	public CombineResults(List<ResultPipe> sources, Sink<Result> output) {
		this.sources = sources;
		this.output = output;
		this.gapMap = new HashMap<>();
	}
	
	public CombineResults(List<ResultPipe> sources, Sink<Result> output, List<PipeSeqConstraint> pipeToSeqenceConstraints) {
		this(sources, output);
		for (PipeSeqConstraint pipeSeqConstraint : pipeToSeqenceConstraints) {
			ResultPipe end = pipeSeqConstraint.end();
			gapMap.put(sources.indexOf(end), pipeSeqConstraint.groupSeqDescription().getSeparation());
		}
	}
	
	@Override
	public void clear() {
		for (Source source : sources) {
			source.clear();
		}
		output.clear();
	}

	// TODO - not great - is there another way to do this?
	public void setId(String id) {
		super.setId(id);
		for (ResultPipe source : sources) {
			source.registerSink(this);
		}
	}
	
	public List<ResultPipe> getSources() {
		return this.sources;
	}

	public Sink<Result> getOutput() {
		return this.output;
	}

	public String description() {
		return "Combine id:[" + getId() + "]"
				+ " sources:" + sources.stream().map(Source::getSourceId).toList() 
				+ " to:[" + output.getSinkId() + "]";
	}

	public void run() {
		// not what we want?
		// given sources : {A(1, 2, 3), B(4, 5, 6), C(7, 8, 9)} 
		// we want: <(1, 4, 7), (1, 4, 8), (1, 4, 9), (1, 5, 7) ...> 
		
		List<List<Result>> sourceBuffers = new ArrayList<>();	// TODO - avoid caching
		for (Source<Result> source : sources) {
			sourceBuffers.add(getAll(source));
		}
		
		int counter = 0;
		for (Result mergedResult : combine(sourceBuffers)) {
			output.put(mergedResult);
			counter++;
		}
		logger.info(description() + " output " + counter + " results");
	}
	
	private List<Result> getAll(Source<Result> source) {
		List<Result> resultList = new ArrayList<>();
		while (source.hasNext()) {
			try {
				resultList.add(source.getNext());
			} catch (Exception e) {
				// TODO
				System.err.println("CME ");
			}
		}
		return resultList;
	}
	
	/**
	 * AI translated code from python. It's just recursively building up the combinations.
	 * 
     * Returns a list of all combinations of argument sequences.
     * For example: combine([1,2], [3,4]) returns [[1, 3], [1, 4], [2, 3], [2, 4]]
     */
    private List<Result> combine(List<List<Result>> input) {
    	logger.fine("Combining " + input);
        List<Result> listout = new ArrayList<>();
        rloop(input, listout, new ArrayList<>(), 0);
        logger.fine("Combined " + listout);
        return listout;
    }

    private void rloop(List<List<Result>> input, List<Result> output, List<Result> combinations, int index) {
        if (index < input.size()) {
        	Integer minSeparation = null;
        	if (index > 0) {
        		if (gapMap.containsKey(index)) {
        			minSeparation = gapMap.get(index);
        		}
        	}
        	
        	List<Result> nextList = input.get(index);
            for (Result item : nextList) {
                
                if (reject(item, combinations, minSeparation)) {
                	logger.fine("NOT Adding " + item + " to " + combinations);
                } else {
                	List<Result> newcombinations = new ArrayList<>(combinations);
                	logger.fine("Adding " + item + " to " + newcombinations);
	                newcombinations.add(item);
	                rloop(input, output, newcombinations, index + 1);
                }
            }
        } else {
        	Result flattened = flatten(combinations);	// TODO - could also flatten as we go ...
        	logger.fine("Flattening " + combinations + " to " + flattened);
            output.add(flattened);
        }
    }
    
    private boolean reject(Result candidate, List<Result> combination, Integer minSeparation) {
    	// TODO - clean up! 
    	if (!combination.isEmpty()) {
    	Result last = combination.get(combination.size() - 1);
//    	for (Result other : combination) {
    		// TODO - make this configurable? or actually 
    		if (last.greaterThanOrEqual(candidate) || candidate.hasSameGroup(last)) {
    			return true;
    		}
    		// TODO - need to clean all this up
    		int sep = last.separation(candidate);
    		if (minSeparation != null && sep > minSeparation) {
//    			System.out.println(sep + "  > " + minSeparation);
				return true;
			} else {
				if (minSeparation != null) {
//					System.out.println(sep + "  < " + minSeparation + " " + last + " " + candidate);
				}
			}
    	}
//    	}
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
