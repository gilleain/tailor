package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.ProteinDescription;
import tailor.match.Match;
import tailor.structure.Structure;

/**
 * Matches descriptions that have multiple chains.
 * 
 * @author maclean
 *
 */
public class MultiChainEngine extends AbstractBaseEngine {

    @Override
    public List<Match> match(Description description, Structure structure) {
        List<Match> results = new ArrayList<>();
        if (description instanceof ProteinDescription) {
            ProteinDescription proteinDescription = 
                (ProteinDescription)description; 
            List<ChainDescription> chainDescriptions = 
                proteinDescription.getChainDescriptions();
        }
        return results;
    }

}
