package tailor.resolve;

import tailor.description.Description;
import tailor.description.ProteinDescription;
import tailor.match.Match;
import tailor.structure.Structure;

/**
 * Resolves descriptions in measures to the descriptions in a match - in other
 * words making a correspondence between the atoms or groups of atoms to be 
 * measured and the actual atoms matched.  
 * 
 * @author maclean
 *
 */
public class Resolver {
    
    public static Structure resolve(Description description, Match match) {
        // TODO : check levels
        if (description instanceof ProteinDescription) {
            return resolve(description.getSubDescriptionAt(0), match.getMatch(0));
        }
        return null;
    }
}
