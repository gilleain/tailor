package tailor.match; // TODO : move to right package

import java.util.ArrayList;
import java.util.List;

import tailor.condition.Condition;
import tailor.description.Description;
import tailor.structure.Level;
import tailor.structure.Structure;

public class Match {
    
    // TODO : why do we need this, exactly?
    private final Level level;  
    
    private final Description description;
    
    private final Structure structure;
    
    private final List<Match> subMatches;
    
    public Match(Description description, Structure structure, Level level) {
        this.description = description;
        this.structure = structure;
        this.level = level;
        this.subMatches = new ArrayList<>();
    }

    /**
     * Checks the match against the conditions in the description.
     * 
     * @param description
     * @return
     */
    public boolean satisfiesConditions(Description description) {
        for (Condition condition : description.getConditions()) {
            if (!condition.satisfiedBy(this)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Adds the substructure in subMatch to the structure in this match.
     * 
     * @param subMatch
     */
    public void completeMatch(Match subMatch) {
        structure.addSubStructure(subMatch.getStructure());
    }
    
    public void addMatch(Match match) {
        this.subMatches.add(match);
    }
    
    public Match getMatch(int i) {
        return subMatches.get(i);
    }
    
    public List<Match> getSubmatches() {
        return subMatches;
    }
    
    public Match associate(Description description, Structure structure) {
        Level sublevel = Level.UNKNOWN; // TODO XXX
        Match childMatch = new Match(description, structure, sublevel);
        subMatches.add(childMatch);
        return childMatch;
    }
    
    public boolean accept(MatchVisitor matchVisitor) {
        if (matchVisitor.visitDescription(description)
            && matchVisitor.visitStructure(structure)) {
            for (Match subMatch : subMatches) {
                if (matchVisitor.visitMatch(subMatch)) {
                    subMatch.accept(matchVisitor);
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public Structure getStructure() {
        return structure;
    }
    
    /**
     * Get the size of the level below.
     * 
     * @return the number of sub-matches
     */
    public int getSize() {
        return subMatches.size();
    }
    
    public Level getLevel() {
        return this.level;
    }
}
