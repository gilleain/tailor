package tailor.engine;

import tailor.description.Description;
import tailor.description.ProteinDescription;

/**
 * Creates, configures, or returns a suitable engine to match a Description
 * to a StructureSource.
 * 
 * @author maclean
 *
 */
public class EngineFactory {

    /**
     * Analyzes the Description, and returns an appropriate Engine. 
     *  
     * @param description
     * @return
     */
    public static Engine getEngine(Description description) {
        int chainCount = 0;
        if (description instanceof ProteinDescription) {
            ProteinDescription proteinDescription = 
                (ProteinDescription) description;
            chainCount = proteinDescription.getChainDescriptions().size();
            if (chainCount == 1) {
                return new SingleChainEngine();
            } else {
                return new MultiChainEngine();
            }
        }
        return null;
    }
}
