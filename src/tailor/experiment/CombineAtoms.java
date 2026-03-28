package tailor.experiment;

import java.util.ArrayList;
import java.util.List;

import tailor.structure.Atom;

/**
 * Combine the output from multiple (?) sources.
 */
public class CombineAtoms {
	
	private List<Source<Atom>> sources;
	
	private Sink<List<Atom>> output;
	
	
	public CombineAtoms(List<Source<Atom>> sources, Sink<List<Atom>> output) {
		this.sources = sources;
		this.output = output;
	}

	public void run() {
		// not what we want?
		// given sources : {A(1, 2, 3), B(4, 5, 6), C(7, 8, 9)} 
		// we want: <(1, 4, 7), (1, 4, 8), (1, 4, 9), (1, 5, 7) ...> 
		
		List<List<Atom>> sourceBuffers = new ArrayList<>();	// TODO - avoid caching
		for (Source<Atom> source : sources) {
			sourceBuffers.add(getAll(source));
		}
		
		for (List<Atom> combination : combine(sourceBuffers)) {
			output.put(combination);
		}
	}
	
	private List<Atom> getAll(Source<Atom> source) {
		List<Atom> atomList = new ArrayList<>();
		while (source.hasNext()) {
			atomList.add(source.getNext());
		}
		return atomList;
	}
	
	/**
	 * AI translated code from python. It's just recursively building up the combinations.
	 * 
     * Returns a list of all combinations of argument sequences.
     * For example: combine([1,2], [3,4]) returns [[1, 3], [1, 4], [2, 3], [2, 4]]
     */
    private static <T> List<List<T>> combine(List<List<T>> seqin) {
        List<List<T>> listout = new ArrayList<>();
        rloop(seqin, listout, new ArrayList<>(), 0);
        return listout;
    }

    private static <T> void rloop(List<List<T>> seqin, List<List<T>> listout, 
                                  List<T> combinations, int index) {
        if (index < seqin.size()) {
        	List<T> nextList = seqin.get(index);
            for (T item : nextList) {
                List<T> newcombinations = new ArrayList<>(combinations);
                newcombinations.add(item);
                rloop(seqin, listout, newcombinations, index + 1);
            }
        } else {
            listout.add(combinations);
        }
    }

}
