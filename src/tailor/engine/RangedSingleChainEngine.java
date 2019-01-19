package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.condition.Condition;
import tailor.condition.PropertyCondition;
import tailor.description.Description;
import tailor.description.RangedGroupDescription;
import tailor.match.Match;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Level;
import tailor.structure.Structure;

/**
 * Engine that can match GroupDescriptions with ranges. For efficiency reasons,
 * each chain is scanned only once, and a list of partial matches is maintained.
 * 
 * If the range of the description covers the whole chain, this list could grow
 * to be as large as the length of the chain.
 * 
 * @author maclean
 *
 */
public class RangedSingleChainEngine extends AbstractBaseEngine {
    
    private Engine subEngine;
    
    public RangedSingleChainEngine() {
        this.subEngine = new LeafEngine();
    }
    
    public List<Match> match(Description description, Structure structure) {
        List<Match> fullMatches = new ArrayList<>();
        List<Match> partialMatches = new ArrayList<>();
        Level level = structure.getLevel();
        
        // this will be checked for each residue, to generate new partials
        Description firstDescription = description.getSubDescriptions().get(0);
        
        int descriptionLength = getLength(description);
        for (Structure subStructure : structure.getSubstructures()) {
            
            // check for extensions of the partial matches
            int partialsIndex = 0; 
            while (partialsIndex < partialMatches.size()) {
                Match partial = partialMatches.get(partialsIndex);
                int partialLength = getLength(partial);
                boolean extended = false;
                
                // the next group description to match is at index == length
                Description subDescription = 
                    description.getSubDescriptions().get(partialLength);
                
                // check and add
                if (nameMatches(subDescription, subStructure)) {
                    List<Match> atomMatches = 
                        subEngine.match(subDescription, subStructure);
                    if (atomMatches.size() == subDescription.size()) {
                        Level subLevel = subStructure.getLevel();
                        Structure matchingCopy = new Group();
                        matchingCopy.copyProperty(subStructure, "Name");
                        matchingCopy.copyProperty(subStructure, "Number");
                        Match subMatch = new Match(subDescription, matchingCopy, Level.RESIDUE);
                        partial.completeMatch(subMatch);
                        partialLength++;
                        extended = true;
                    }
                }
                
                // the partial may now be complete
                boolean complete = false;
                if (extended && partialLength == descriptionLength) {
                    fullMatches.add(partial);
                    complete = true;
                }
                
                // remove those partials that have not been extended
                if (!extended || complete) {
                    partialMatches.remove(partialsIndex);
                } else {
                    partialsIndex++;
                }
            }
            
            // check for new matches
            if (nameMatches(firstDescription, subStructure)) {
                List<Match> atomMatches = 
                    subEngine.match(firstDescription, subStructure);
                if (atomMatches.size() == firstDescription.size()) {
                    Structure partialStructure = new Chain();
                    
                    // TODO : chain specific!
                    partialStructure.setProperty("Name", "A");
                    
                    Match partial = new Match(description, partialStructure, Level.CHAIN);
                    Level subLevel = subStructure.getLevel();
                    Structure matchingCopy = new Group();
                    matchingCopy.copyProperty(subStructure, "Name");
                    matchingCopy.copyProperty(subStructure, "Number");
                    Match subMatch = new Match(firstDescription, matchingCopy, Level.RESIDUE);
                    partial.completeMatch(subMatch);
                    partialMatches.add(partial);
                }
            }
        }
        return fullMatches;
    }
    
    private boolean nameMatches(Description description, Structure structure) {
        String name = null;
        for (Condition condition : description.getConditions()) {
            if (condition instanceof PropertyCondition) {
                PropertyCondition prop = (PropertyCondition) condition;
                if (prop.keyEquals("Name")) {
                    name = prop.getValue();
                    break;
                }
            }
        }
        if (name != null) {
            return structure.hasPropertyEqualTo("Name", name);
        }
        return true;
    }
   
    
    private int getLength(Description chainDescription) {
        int count = 0;
        for (Description groupDescription : chainDescription.getSubDescriptions()) {
            if (groupDescription instanceof RangedGroupDescription) {
                RangedGroupDescription rgd = 
                    (RangedGroupDescription) groupDescription;
                count += rgd.range();
            } else {
                count++;
            }
        }
        return count;
    }
    
    private int getLength(Match partial) {
        return partial.getSize();
    }

}
