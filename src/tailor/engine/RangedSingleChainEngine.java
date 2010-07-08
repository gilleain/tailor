package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.Level;
import tailor.datasource.Structure;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;
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
    
    @Override
    public List<Match> match(Description description, Structure structure) {
        List<Match> matches = new ArrayList<Match>();
        if (description instanceof ProteinDescription) {
            ProteinDescription proteinDescription =
                (ProteinDescription) description;
            ChainDescription chainDescription = 
                proteinDescription.getChainDescriptions().get(0);
            for (Structure chain : structure) {
                scan(chainDescription, chain);
            }
        }
        return matches;
    }
    
    public List<Match> scan(ChainDescription chainDescription, Structure chain) {
        List<Match> fullMatches = new ArrayList<Match>();
        List<Match> partialMatches = new ArrayList<Match>();
        
        // this will be checked for each residue, to generate new partials
        GroupDescription firstGroupDescription = 
            chainDescription.getGroupDescriptions().get(0);
        
        int descriptionLength = getLength(chainDescription);
        for (Structure residue : chain) {
            
            // check for extensions of the partial matches
            int partialsIndex = 0; 
            while (partialsIndex < partialMatches.size()) {
                Match partial = partialMatches.get(partialsIndex);
                int partialLength = getLength(partial);
                boolean extended = false;
                
                // the next group description to match is at index == length
                GroupDescription groupDescription = 
                    chainDescription.getGroupDescriptions().get(partialLength);
                
                // check and add
                if (groupDescription.nameMatches(residue)) {
                    Structure matchingResidue = 
                        groupDescription.matchTo(residue);
                    if (groupDescription.fullyMatches(matchingResidue)) {
                        completeMatch(groupDescription, matchingResidue, 
                                partial, residue);
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
            if (firstGroupDescription.nameMatches(residue)) {
                Structure matchingResidue = 
                    firstGroupDescription.matchTo(residue);
                if (firstGroupDescription.fullyMatches(matchingResidue)) {
                    Structure partialStructure = new Structure(Level.CHAIN);
                    partialStructure.setProperty("Name", "A");
                    Match partial = new Match(chainDescription, partialStructure);
                    completeMatch(firstGroupDescription, matchingResidue, 
                            partial, residue);
                    partialMatches.add(partial);
                }
            }
        }
        return fullMatches;
    }
    
    private void completeMatch(GroupDescription description, 
            Structure matchingResidue, Match chain, Structure residue) {
        matchingResidue.setProperty("Name", residue.getProperty("Name"));
        matchingResidue.setProperty("Number", residue.getProperty("Number"));
        chain.getStructure().addSubStructure(matchingResidue);
        chain.associate(description, matchingResidue);
    }
    
    private int getLength(ChainDescription chainDescription) {
        int count = 0;
        for (GroupDescription groupDescription : chainDescription) {
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
