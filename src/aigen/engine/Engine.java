package aigen.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import aigen.description.ChainDescription;
import aigen.description.Description;
import aigen.description.DescriptionException;
import aigen.description.ResidueDescription;
import aigen.feature.Atom;
import aigen.feature.Chain;
import aigen.feature.Residue;
import aigen.feature.Structure;

/**
 * This module provides:
 *  - a Matcher object to match abstract Descriptions to Structures
 *  - lookup(description, structure) function to find concrete Descriptions in Structures.
 */
public class Engine {
    private static final Logger logger = Logger.getLogger(Engine.class.getName());

    static {
        logger.setLevel(Level.INFO);
    }
    
    class Pair<F, S> {
    	public final F first;
    	public final S second;

    	public Pair(F first, S second) {
    		this.first = first;
    		this.second = second;
    	}
    }

    public static Atom lookup(String descriptionA, Object structure) throws DescriptionException {
		// TODO remove this when types align
		return null;
	}

	/**
     * Return part of an example (which is itself a fragment of a whole structure)
     * given a description of what to look for.
     */
    public static Atom lookup(Description description, Object example) throws DescriptionException {
    	// TODO - could use instanceof or a visitor?
        if (description.getLevelCode().equals("C")) {
            return lookupChain((ChainDescription) description, (Structure) example);
        } else if (description.getLevelCode().equals("R")) {
            return lookupResidue((ResidueDescription) description, (Chain) example);
        }
        return null;
    }

    private static Atom lookupChain(ChainDescription chainDescription, Structure structureExample) 
            throws DescriptionException {
        // TODO: what if there is more than one chain?
        // TODO: this could really do with not being a generator!
        List<Chain> chains = structureExample.chainsOfType(chainDescription.getChainType());
        if (chains.isEmpty()) {
            throw new DescriptionException("No chains of type " + chainDescription.getChainType());
        }
        return lookupResidue(chainDescription.getResidueDescriptions().get(0), chains.get(0));
    }

    private static Atom lookupResidue(ResidueDescription residueDescription, Chain chainExample) 
            throws DescriptionException {
        aigen.description.AtomDescription atomDescription = residueDescription.getAtomDescriptions().get(0);
        Residue residue = chainExample.getResidue(residueDescription.getPosition() - 1);
        
        for (Atom atom : residue.getAtoms()) {
            if (atomDescription.getName().equals(atom.getName())) {
                return atom;
            }
        }
        return null;
    }

    /**
     * Given a list like [(0, 1), (0, 1), (0, 1)] will return all combinations.
     * This should be something like:
     *     [0, 0, 0]
     *     [0, 0, 1]
     *     [0, 1, 0]
     *     [0, 1, 1]
     *     [1, 0, 0]
     *     [1, 0, 1]
     *     [1, 1, 0]
     *     [1, 1, 1]
     */
    public static List<List<Integer>> generateGaps(List<Pair<Integer, Integer>> pairlist) {
        return recursivelyGenerateGaps(pairlist);
    }

    /**
     * A generator for lists of numbers.
     */
    private static List<List<Integer>> recursivelyGenerateGaps(List<Pair<Integer, Integer>> pairlist) {
        List<List<Integer>> result = new ArrayList<>();
        
        if (pairlist.size() > 1) {
            int min = pairlist.get(0).first;
            int max = pairlist.get(0).second;
            
            for (int i = min; i <= max; i++) {
                List<Pair<Integer, Integer>> remaining = pairlist.subList(1, pairlist.size());
                List<List<Integer>> rest = recursivelyGenerateGaps(remaining);
                
                for (List<Integer> restList : rest) {
                    List<Integer> newList = new ArrayList<>();
                    newList.add(i);
                    newList.addAll(restList);
                    result.add(newList);
                }
            }
        } else {
            int min = pairlist.get(0).first;
            int max = pairlist.get(0).second;
            
            for (int i = min; i <= max; i++) {
                List<Integer> singleList = new ArrayList<>();
                singleList.add(i);
                result.add(singleList);
            }
        }
        
        return result;
    }

    /**
     * Returns a list of all combinations of argument sequences.
     * For example: combine([1,2], [3,4]) returns [[1, 3], [1, 4], [2, 3], [2, 4]]
     */
    @SafeVarargs
    public static <T> List<List<T>> combine(List<T>... seqin) {
        List<List<T>> listout = new ArrayList<>();
        rloop(seqin, listout, new ArrayList<>(), 0);
        return listout;
    }

    private static <T> void rloop(List<T>[] seqin, List<List<T>> listout, 
                                  List<T> combinations, int index) {
        if (index < seqin.length) {
            for (T item : seqin[index]) {
                List<T> newcombinations = new ArrayList<>(combinations);
                newcombinations.add(item);
                rloop(seqin, listout, newcombinations, index + 1);
            }
        } else {
            listout.add(combinations);
        }
    }
}
