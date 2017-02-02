package tailor.match; // TODO : move to right package

import java.util.ArrayList;
import java.util.List;

import tailor.description.Description;
import tailor.structure.Structure;

public class Match {
    
    private final Description description;
    
    private final Structure structure;
    
    private final List<Match> subMatches;
    
    public Match(Description description, Structure structure) {
        this.description = description;
        this.structure = structure;
        this.subMatches = new ArrayList<Match>();
    }
    
    public void addMatch(Match match) {
        this.subMatches.add(match);
    }
    
    public Match associate(Description description, Structure structure) {
        Match childMatch = new Match(description, structure);
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
    public int getLevelSize() {
        return subMatches.size();
    }
}
