package tailor.engine;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import tailor.condition.Condition;
import tailor.condition.PropertyCondition;
import tailor.datasource.ResultsPrinter;
import tailor.datasource.StructureSource;
import tailor.description.Description;
import tailor.match.Match;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Level;
import tailor.structure.Structure;

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

        List<Structure> groups = structure.getSubstructures();
        int lastPossibleStart = groups.size() - span;
        for (int start = 0; start < lastPossibleStart; start++) {
            // starting at this position, scan the groups 
            Match chainMatch = scan(description, groups, start);

            if (chainMatch != null && chainMatch.getSize() == span
//                    ){
                    && chainMatch.satisfiesConditions(description)) {
                Chain matchedChain = (Chain) chainMatch.getStructure();
                matchedChain.setName(((Chain)structure).getName());
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
        Chain chain = new Chain();
        Match match = new Match(description, chain, Level.CHAIN);
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
                    // a copy is made, so that only the matching atoms are stored
                    Group matchingCopy = new Group();
                    matchingCopy.setId(((Group)group).getId());
                    matchingCopy.setNumber(((Group)group).getNumber());

                    Match subMatch = new Match(subDescription, matchingCopy, Level.RESIDUE);
                    for (Match atomMatch : atomMatches) {
                        subMatch.addMatch(atomMatch);
                        subMatch.completeMatch(atomMatch);
                    }
                    if (subMatch.satisfiesConditions(subDescription)) {
                        chain.addGroup(matchingCopy);
                        match.addMatch(subMatch);
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
                        prop.valueEquals(((Group)structure).getName())) {
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
