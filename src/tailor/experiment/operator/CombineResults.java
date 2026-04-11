package tailor.experiment.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.plan.Result;

/**
 * Combine the output from multiple sources, flattening them into a list of results.
 */
public class CombineResults extends AbstractOperator {
	
	private static Logger logger = Logger.getLogger(CombineResults.class.getName());
	
	private List<ResultPipe> sources;
	
	private Sink<Result> output;
	
	public CombineResults(List<ResultPipe> sources, ResultPipe output) {
		this.sources = sources;
		this.output = output;
		for (ResultPipe source : sources) {
			source.registerSink(this);
		}
		output.registerSource(this);
	}
	
	public CombineResults(List<ResultPipe> sources, Sink<Result> output) {
		this.sources = sources;
		this.output = output;
		for (ResultPipe source : sources) {
			source.registerSink(this);
		}
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
			resultList.add(source.getNext());
		}
		return resultList;
	}
	
	/**
	 * AI translated code from python. It's just recursively building up the combinations.
	 * 
     * Returns a list of all combinations of argument sequences.
     * For example: combine([1,2], [3,4]) returns [[1, 3], [1, 4], [2, 3], [2, 4]]
     */
    private static List<Result> combine(List<List<Result>> seqin) {
    	logger.fine("Combining " + seqin);
        List<Result> listout = new ArrayList<>();
        rloop(seqin, listout, new ArrayList<>(), 0);
        logger.fine("Combined " + listout);
        return listout;
    }

    private static void rloop(List<List<Result>> seqin, List<Result> listout, List<Result> combinations, int index) {
        if (index < seqin.size()) {
        	List<Result> nextList = seqin.get(index);
            for (Result item : nextList) {
                
                if (reject(item, combinations)) {
                	logger.fine("NOT Adding " + item + " to " + combinations);
                } else {
                	List<Result> newcombinations = new ArrayList<>(combinations);
                	logger.fine("Adding " + item + " to " + newcombinations);
	                newcombinations.add(item);
	                rloop(seqin, listout, newcombinations, index + 1);
                }
            }
        } else {
        	Result flattened = flatten(combinations);	// TODO - could also flatten as we go ...
        	logger.fine("Flattening " + combinations + " to " + flattened);
            listout.add(flattened);
        }
    }
    
    private static boolean reject(Result result, List<Result> combination) {
    	// this is a bit odd - checking that _none_ of the results in combo have the same group
    	for (Result other : combination) {
    		// TODO - make this configurable? or actually 
    		if (other.greaterThanOrEqual(result) || result.hasSameGroup(other)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private static Result flatten(List<Result> combinations) {
    	Result mergedResult = combinations.get(0).copy();
    	for (int index = 1; index < combinations.size(); index++) {
    		mergedResult = mergedResult.merge(combinations.get(index));
    	}
    	return mergedResult;
    }

}
