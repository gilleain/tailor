package tailor.experiment.operator;

import java.util.ArrayList;
import java.util.List;

import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.plan.Result;

/**
 * Combine the output from multiple sources, flattening them into a list of results.
 */
public class CombineResults extends AbstractOperator {
	
	private List<ResultPipe> sources;
	
	private Sink<Result> output;
	
	public CombineResults(String id, List<ResultPipe> sources, ResultPipe output) {
		this.id = id;
		this.sources = sources;
		this.output = output;
		for (ResultPipe source : sources) {
			source.registerSink(this);
		}
		output.registerSource(this);
	}
	
	public CombineResults(String id, List<ResultPipe> sources, Sink<Result> output) {
		this.id = id;
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
		
		for (Result mergedResult : combine(sourceBuffers)) {
			output.put(mergedResult);
		}
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
    	System.out.println("Combining " + seqin);
        List<Result> listout = new ArrayList<>();
        rloop(seqin, listout, new ArrayList<>(), 0);
        System.out.println("Combined " + listout);
        return listout;
    }

    private static void rloop(List<List<Result>> seqin, List<Result> listout, List<Result> combinations, int index) {
        if (index < seqin.size()) {
        	List<Result> nextList = seqin.get(index);
            for (Result item : nextList) {
                
                if (containedIn(item, combinations)) {
                	System.out.println("NOT Adding " + item + " to " + combinations);
                } else {
                	List<Result> newcombinations = new ArrayList<>(combinations);
	                System.out.println("Adding " + item + " to " + newcombinations);
	                newcombinations.add(item);
	                rloop(seqin, listout, newcombinations, index + 1);
                }
            }
        } else {
        	Result flattened = flatten(combinations);	// TODO - could also flatten as we go ...
        	System.out.println("Flattening " + combinations + " to " + flattened);
            listout.add(flattened);
        }
    }
    
    private static boolean containedIn(Result result, List<Result> combination) {
    	// this is a bit odd - checking that _none_ of the results in combo have the same group
    	for (Result other : combination) {
    		if (result.hasSameGroup(other)) {
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
