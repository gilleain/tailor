package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.Level;
import tailor.datasource.Structure;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;

/**
 * Matches descriptions that only cover a single chain. If the structure has
 * multiple chains, each chain of the structure wil be compared to the 
 * description in turn.  
 * 
 * @author maclean
 *
 */
public class SingleChainEngine extends AbstractBaseEngine implements Engine {

    @Override
    public List<Structure> match(Description description, Structure structure) {
        List<Structure> results = new ArrayList<Structure>();
        if (description instanceof ProteinDescription) {
            ProteinDescription proteinDescription = 
                (ProteinDescription)description; 
            List<ChainDescription> chainDescriptions = 
                proteinDescription.getChainDescriptions();
            
            // sanity check
            if (chainDescriptions.size() == 1) {
                ChainDescription chainDescription = chainDescriptions.get(0); 
                for (Structure chain : structure.getSubStructures()) {
                    for (Structure chainMatch : scan(chainDescription, chain)) {
                        Structure motif = new Structure(Level.PROTEIN);
                        motif.setProperty("Name", structure.getId());
                        motif.addSubStructure(chainMatch);
                        results.add(motif);
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
    public List<Structure> scan(
            ChainDescription chainDescription, Structure chain) {
        List<Structure> matches = new ArrayList<Structure>();

        int span = chainDescription.last().getOffset();

        List<Structure> groups = chain.getSubStructures();
        int lastPossibleStart = groups.size() - span;
        for (int start = 0; start < lastPossibleStart; start++) {
            // starting at this position, scan the groups 
            Structure chainMatch = scan(chainDescription, groups, start);

            // only if we get a match of sufficient size
            // is it worthwhile to consider any conditions
            if (chainMatch.size() == chainDescription.size()) {
                if (chainDescription.conditionsSatisfied(chainMatch)) {
                    chainMatch.setProperty("Name", chain.getProperty("Name"));
                    matches.add(chainMatch);
                }
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
    public Structure scan(ChainDescription chainDescription, 
                            List<Structure> groups, int start) {
         Structure chain = new Structure(Level.CHAIN);
         for (GroupDescription groupDescription : 
             chainDescription.getGroupDescriptions()) {
             int offset = groupDescription.getOffset();
             Structure group = groups.get(start + offset);
             if (groupDescription.matches(group)) {

                 // returns a new group filled with 
                 // as many matching atoms as possible
                 Structure groupMatch = groupDescription.matchTo(group);
                 
                 // only if we get a match of sufficient size
                 // is it worthwhile to consider any conditions
                 if (groupMatch.size() == groupDescription.size()) {
                     if (groupDescription.conditionsSatisfied(groupMatch)) {
                         groupMatch.setProperty("Name", 
                                 group.getProperty("Name"));
                         groupMatch.setProperty("Number", 
                                 group.getProperty("Number"));
                         chain.addSubStructure(groupMatch);
                     }
                 }
             } else {
                 return chain;
             }
         }
         return chain;
     }

}
