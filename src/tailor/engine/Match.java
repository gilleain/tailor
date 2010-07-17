package tailor.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tailor.Level;
import tailor.condition.Condition;
import tailor.datasource.Structure;
import tailor.description.Description;

/**
 * A search will produce a series of Match objects, that stores the association 
 * between the Description and the Structure it matches. 
 * 
 * @author maclean
 *
 */
public class Match implements Iterable<Match> {
    
    private Level level;
    
    private Description description;
    
    private Structure structure;
    
    private List<Match> subMatches;

    public Match(Description description, Structure structure) {
        this.description = description;
        this.structure = structure;
        this.subMatches = new ArrayList<Match>();
        
        // TODO : check the description level against the structure level
        this.level = description.getLevel();
    }
    
    public Iterator<Match> iterator() {
        return subMatches.iterator();
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    /**
     * Lookup the structure in the match corresponding to the description with
     * the id <code>id</code>.
     * 
     * @param id
     * @return
     */
    public Structure getStructureByDescriptionID(int id) {
        System.out.println("checking " + id + " against " + description.getID());
        if (description.getID() == id) {
            return structure;
        } else {
            for (Match subMatch : subMatches) {
                Structure found = subMatch.getStructureByDescriptionID(id);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    
    /**
     * Checks the match against the conditions in the description.
     * 
     * @param description
     * @return
     */
    public boolean satisfiesConditions(Description description) {
        for (Condition condition : description.getConditions()) {
            if (condition.satisfiedBy(this)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
    
    public Match getSubMatch(int i) {
        return subMatches.get(i);
    }
    
    /**
     * Make a sub-match between a sub-description and a sub-structure.
     * Returns a reference to the newly created Match.
     *  
     * @param description
     * @param structure
     */
    public Match associate(Description description, Structure structure) {
        Match childMatch = new Match(description, structure);
        subMatches.add(childMatch);
        return childMatch;
    }
    
    /**
     * Adds the substructure in subMatch to the structure in this match.
     * 
     * @param subMatch
     */
    public void completeMatch(Match subMatch) {
        structure.addSubStructure(subMatch.getStructure());
    }
    
    public void addSubMatch(Match match) {
        this.subMatches.add(match);
    }
    
    public boolean hasPropertyEqualTo(String propertyKey, String propertyValue) {
        return structure.hasPropertyEqualTo(propertyKey, propertyValue);
    }

    public Description getDescription() {
        return this.description;
    }
    
    public Structure getStructure() {
        return this.structure;
    }
    
    public int size() {
        return this.subMatches.size();
    }
    
    public String toString() {
        return description + " matches " + structure.getId();
    }
    
}
