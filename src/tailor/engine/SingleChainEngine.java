package tailor.engine;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import tailor.Level;
import tailor.condition.Condition;
import tailor.datasource.ResultsPrinter;
import tailor.datasource.Structure;
import tailor.datasource.StructureSource;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;

/**
 * Matches descriptions that only cover a single chain. If the structure has
 * multiple chains, each chain of the structure will be compared to the 
 * description in turn.  
 * 
 * @author maclean
 *
 */
public class SingleChainEngine extends AbstractBaseEngine implements Engine {

    private GroupEngine groupEngine;
    
    public SingleChainEngine() {
        super();
        this.groupEngine = new GroupEngine();
    }
    
    public SingleChainEngine(ResultsPrinter resultsPrinter,
            PrintStream errStream, StructureSource structureSource) {
        super(resultsPrinter, errStream, structureSource);
        this.groupEngine = new GroupEngine();
    }

    @Override
    public List<Match> match(Description description, Structure structure) {
        List<Match> results = new ArrayList<Match>();
        if (description instanceof ProteinDescription) {
            ProteinDescription proteinDescription = 
                (ProteinDescription)description; 
            List<ChainDescription> chainDescriptions = 
                proteinDescription.getChainDescriptions();
            
            // sanity check
            if (chainDescriptions.size() == 1) {
                ChainDescription chainDescription = chainDescriptions.get(0);
                
                // XXX assumes that the structure passed in is a protein!
                for (Structure chain : structure) {
                    for (Match chainMatch : scan(chainDescription, chain)) {
                        // make a structure for the match
                        Structure motif = new Structure(Level.PROTEIN);
                        motif.setProperty("Name", structure.getId());
                        
                        // add the chain structure match to the top
                        Structure matchedChain = chainMatch.getStructure();
                        motif.addSubStructure(matchedChain);
                        
                        // associate at the top level
                        Match match = new Match(description, motif);
                        match.addSubMatch(chainMatch);
                        results.add(match);
                    }
                }
            } else {
                // TODO otherwise? throw an error?
            }
        } else {
            // TODO otherwise? throw an error?
        }
        return results;
    }
    
    /**
     * Scan (match) a ChainDescription to a piece of Structure representing a
     * chain and return a list of matches.
     * 
     * @param chainDescription
     * @param chain
     * @return
     */
    public List<Match> scan(ChainDescription chainDescription, Structure chain) {
        List<Match> matches = new ArrayList<Match>();

        int span = chainDescription.size();

        List<Structure> groups = chain.getSubStructures();
        int lastPossibleStart = groups.size() - span;
        for (int start = 0; start < lastPossibleStart; start++) {
            // starting at this position, scan the groups 
            Match chainMatch = scan(chainDescription, groups, start);

            if (fullMatch(chainDescription, chainMatch)) {
                Structure matchedChain = chainMatch.getStructure();
                matchedChain.setProperty("Name", chain.getProperty("Name"));
                matches.add(chainMatch);
            }
        }

        return matches;
    }
    
    /**
     * Scan (match) a ChainDescription to a list of Structures that represent
     * groups (or 'residues') starting at position <code>start</code>.
     * 
     * @param chainDescription
     * @param groups
     * @param start
     * @return
     */
    public Match scan(ChainDescription chainDescription, 
                            List<Structure> groups, int start) {
         Structure chain = new Structure(Level.CHAIN);
         Match chainMatch = new Match(chainDescription, chain);
         for (int index = 0; index < chainDescription.size(); index++) {
             GroupDescription groupDescription = 
                 chainDescription.getGroupDescription(index);
             Structure group = groups.get(start + index);
             if (groupDescription.nameMatches(group)) {

                 // returns a new group filled with 
                 // as many matching atoms as possible
                 Match groupMatch = 
                     groupEngine.match(groupDescription, group).get(0);
                 
                 // only if we get a match of sufficient size
                 // is it worthwhile to consider any conditions
                 if (groupEngine.fullMatch(groupDescription, groupMatch)) {
                     chain.addSubStructure(groupMatch.getStructure());
                     chainMatch.addSubMatch(groupMatch);
                 }
             } else {
                 return chainMatch;
             }
         }
         return chainMatch;
     }
    
    public boolean fullMatch(ChainDescription chainDescription, Match match) {
        // only if we get a match of sufficient size
        // is it worthwhile to consider any conditions
        return chainDescription.size() == match.size()
            && conditionsSatisfied(chainDescription, match);
    }
    
    // TODO : put this in a base class
    public boolean conditionsSatisfied(Description description, Match match) {
        for (Condition condition : description.getConditions()) {
            if (condition.satisfiedBy(match)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
}
