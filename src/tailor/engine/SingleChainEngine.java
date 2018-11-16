package tailor.engine;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import tailor.condition.Condition;
import tailor.condition.PropertyCondition;
import tailor.datasource.ResultsPrinter;
import tailor.datasource.Structure;
import tailor.datasource.StructureSource;
import tailor.description.Description;
import tailor.structure.Level;

/**
 * Matches descriptions that only cover a single chain. If the structure has
 * multiple chains, each chain of the structure will be compared to the 
 * description in turn.  
 * 
 * @author maclean
 *
 */
public class SingleChainEngine extends AbstractBaseEngine implements Engine {

    private Engine atomEngine;

    public SingleChainEngine() {
        super();
        this.atomEngine = new LeafEngine();
    }

    public SingleChainEngine(ResultsPrinter resultsPrinter,
            PrintStream errStream, StructureSource structureSource) {
        super(resultsPrinter, errStream, structureSource);
        this.atomEngine = new LeafEngine();
    }
    
    @Override
    public List<Match> match(Description description, Structure structure) {
        List<Match> matches = new ArrayList<>();

        int span = description.size();

        List<Structure> groups = structure.getSubStructures();
        int lastPossibleStart = groups.size() - span;
        for (int start = 0; start < lastPossibleStart; start++) {
            // starting at this position, scan the groups 
            Match chainMatch = scan(description, groups, start);

            if (chainMatch != null && chainMatch.size() == span
//                    ){
                    && chainMatch.satisfiesConditions(description)) {
                Structure matchedChain = chainMatch.getStructure();
                matchedChain.setProperty("Name", structure.getProperty("Name"));
                matches.add(chainMatch);
            }
        }

        return matches;
    }


    /**
     * Scan (match) a ChainDescription to a list of Structures that represent
     * groups (or 'residues') starting at position <code>start</code>.
     * 
     * @param description
     * @param groups
     * @param start
     * @return
     */
    public Match scan(Description description, List<Structure> groups, int start) {
        Structure chain = new Structure(Level.CHAIN);
        Match match = new Match(description, chain);
        for (int index = 0; index < description.size(); index++) {
            boolean hasMatched = false;
            Description subDescription = 
                description.getSubDescriptionAt(index);
            Structure group = groups.get(start + index);
            if (nameMatches(subDescription, group)) {

                List<Match> atomMatches = 
                    atomEngine.match(subDescription, group);

                // only if we get a match of sufficient size
                // is it worthwhile to consider any conditions
                if (atomMatches.size() == subDescription.size()) {
                    Level level = Level.RESIDUE;
                    // a copy is made, so that 
                    // only the matching atoms are stored
                    Structure matchingCopy = new Structure(level);
                    matchingCopy.copyProperty(group, "Name");
                    //   XXX groups only!
                    matchingCopy.copyProperty(group, "Number");

                    Match subMatch = new Match(subDescription, matchingCopy);
                    for (Match atomMatch : atomMatches) {
                        subMatch.addSubMatch(atomMatch);
                        subMatch.completeMatch(atomMatch);
                    }
                    if (subMatch.satisfiesConditions(subDescription)) {
                        chain.addSubStructure(matchingCopy);
                        match.addSubMatch(subMatch);
                        hasMatched = true;
                    } 
                } 
            }
            if (!hasMatched) {
                return null;
            }
        }
        return match;
    }
    
    // TODO : expensive!
    private boolean nameMatches(Description description, Structure structure) {
        for (Condition condition : description.getConditions()) {
            if (condition instanceof PropertyCondition) {
                PropertyCondition prop = (PropertyCondition) condition;
                if (prop.keyEquals("Name") && 
                        prop.valueEquals(structure.getProperty("Name"))) {
                    return true;
                } else {
                    // there is a Name condition, but it doesn't match
                    return false;
                }
            }
        }
        // true if there are no property conditions with a key of 'Name'
        return true;
    }
   
}
