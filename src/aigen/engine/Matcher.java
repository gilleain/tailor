package aigen.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import aigen.condition.Condition;
import aigen.description.ChainDescription;
import aigen.description.Description;
import aigen.description.StructureDescription;
import aigen.feature.Chain;
import aigen.feature.Model;
import aigen.feature.Residue;
import aigen.feature.Structure;

class Matcher {
    private static final Logger logger = Logger.getLogger(Matcher.class.getName());
    private Description description;

    public Matcher(Description description) {
        this.description = description;
        logger.log(Level.FINE, "matcher with description " + description);
    }

    /**
     * Use the description of this matcher to find all examples in structure.
     */
    public List<Structure> findAll(Structure structure) {
        List<Structure> matches = new ArrayList<>();

        // top level is Structure -> search through combinations of Chains
        if (description.getLevelCode().equals("S")) {
            logger.log(Level.FINE, "structure description");
            // TODO - seems odd to be casting here
            StructureDescription structureDescription = (StructureDescription) description;

            // select chains of a particular type and get matches
            Map<String, List<Chain>> matchMap = new HashMap<>();
            List<List<String>> chainIDLists = new ArrayList<>();
            
            for (ChainDescription chainDescription : structureDescription.getChainDescriptions()) {
                List<String> chainIDs = new ArrayList<>();
                for (Chain chain : structure.chainsOfType(chainDescription.getChainType())) {
                    matchMap.put(chain.getChainID(), 
                               findMatchesInChain(chainDescription, chain));
                    logger.log(Level.FINE, "adding chain " + chain.getChainID());
                    chainIDs.add(chain.getChainID());
                }
                chainIDLists.add(chainIDs);
            }

            // work on tuples of chains
            List<List<String>> chainCombinations = Engine.combine(
                chainIDLists.toArray(new List[0]));
            
            for (List<String> chainCombo : chainCombinations) {
                logger.log(Level.FINE, "trying chains: " + chainCombo);

                // combine these matches into whole motifs
                // TODO XXX this should really be cross-product!
                // currently only works with pairs...
                for (Chain matchA : matchMap.get(chainCombo.get(0))) {
                    for (Chain matchB : matchMap.get(chainCombo.get(1))) {

                        // XXX AARGH! : we make the fragment before testing??!
                        Structure fragment = new Structure(description.getName());
                        Model model = new Model(0);
                        fragment.add(model);
                        model.add(matchA);
                        model.add(matchB);

                        if (matchConditions(fragment)) {
                            matches.add(fragment);
                        }
                    }
                }
            }

        // top level is Chain -> search through combinations of Residues
        } else if (description.getLevelCode().equals("C")) {
            logger.log(Level.FINE, "chain description");
            ChainDescription chainDescription = (ChainDescription) description;

            // find the next suitable chain to look through
            for (Chain chain : structure.chainsOfType(chainDescription.getChainType())) {
                logger.log(Level.FINE, "finding matches in chain " + chain.getChainID());
                List<Chain> possibles = findMatchesInChain(description, chain);
                for (Chain p : possibles) {
                    if (matchConditions(p)) {
//                        matches.add((Structure) p);	// TODO - ?
                    }
                }
            }
        }
        
        return matches;
    }

    // XXX this is surely mis-named!
    private boolean matchConditions(Object fragment) {
        for (Object o : description.getConditions()) {
        	Condition condition = (Condition)o;	// TODO
            logger.log(Level.FINE, "checking condition " + condition);
            
            if (condition.hasProperty("propertyKey") && 
                condition.getProperty("propertyKey").equals("chainID")) {
                continue;
            }
            
            if (condition.satisfiedBy(fragment)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    private List<Chain> findMatchesInChain(Description chainDescription, Chain chain) {
        List<Chain> chainMatches = new ArrayList<>();

        // the maximum index a match can start at
        int descriptionLength = chainDescription.size();
        int maxIndex = chain.size() - descriptionLength;

        for (int startIndex = 0; startIndex <= maxIndex; startIndex++) {

            // its cheaper to store the indices than to construct partial matches
            List<Integer> matchingIndices = new ArrayList<>();
            
            List<Description> residueDescriptions = chainDescription.getChildren();
            for (int i = 0; i < residueDescriptions.size(); i++) {
                Description residueDescription = residueDescriptions.get(i);
                int currentIndex = startIndex + i;
                
                if (matchResidue(residueDescription, chain.getResidue(currentIndex))) {
                    matchingIndices.add(currentIndex);
                } else {
                    break;
                }
            }

            // store whole matches
            if (matchingIndices.size() == descriptionLength) {
                Chain chainExample = new Chain(chain.getChainID(), chain.getChainType());
                for (int index : matchingIndices) {
                	// TODO
//                    chainExample.addResidue(chain.getResidue(index).copy());
                }
                logger.log(Level.FINE, "storing " + chainExample);
                chainMatches.add(chainExample);
            }
        }
        
        return chainMatches;
    }

    // TODO : this needs to be implemented!
    private boolean matchResidue(Description residueDescription, Residue residue) {
        logger.log(Level.FINE, residueDescription + " -> " + residue);
        return true;
    }
}
