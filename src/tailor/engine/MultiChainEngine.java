package tailor.engine;

import java.util.ArrayList;
import java.util.List;

import tailor.datasource.Structure;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.ProteinDescription;

/**
 * Matches descriptions that have multiple chains.
 * 
 * @author maclean
 *
 */
public class MultiChainEngine extends AbstractBaseEngine {

    @Override
    public List<Structure> match(Description description, Structure structure) {
        List<Structure> results = new ArrayList<Structure>();
        if (description instanceof ProteinDescription) {
            ProteinDescription proteinDescription = 
                (ProteinDescription)description; 
            List<ChainDescription> chainDescriptions = 
                proteinDescription.getChainDescriptions();
        }
        return results;
    }

}
