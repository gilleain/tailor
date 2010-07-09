package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.Level;
import tailor.condition.Condition;
import tailor.condition.PropertyCondition;
import tailor.datasource.Structure;
import tailor.description.Description;
import tailor.description.RangedGroupDescription;

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
    
    private GroupEngine subEngine;
    
    public RangedSingleChainEngine() {
        this.subEngine = new GroupEngine();
    }
    
    public List<Match> match(Description description, Structure structure) {
        List<Match> fullMatches = new ArrayList<Match>();
        List<Match> partialMatches = new ArrayList<Match>();
        Level level = structure.getLevel();
        
        // this will be checked for each residue, to generate new partials
        Description firstDescription = description.getSubDescriptions().get(0);
        
        int descriptionLength = getLength(description);
        for (Structure subStructure : structure) {
            
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
                    Match matchingCopy = 
                        subEngine.match(subDescription, subStructure).get(0);
                    if (subEngine.fullMatch(subDescription, matchingCopy)) {
                        partial.completeMatch(matchingCopy);
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
                Match matchingResidue = 
                    subEngine.match(firstDescription, subStructure).get(0);
                if (subEngine.fullMatch(firstDescription, matchingResidue)) {
                    Structure partialStructure = new Structure(level);
                    partialStructure.setProperty("Name", "A");
                    Match partial = new Match(description, partialStructure);
                    partial.completeMatch(matchingResidue);
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
        return partial.size();
    }

}
