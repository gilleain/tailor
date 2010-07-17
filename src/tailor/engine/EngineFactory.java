package tailor.engine;

import java.io.PrintStream;

import tailor.datasource.ResultsPrinter;
import tailor.datasource.StructureSource;
import tailor.description.Description;
import tailor.description.ProteinDescription;
import tailor.validate.DescriptionValidator;

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
        DescriptionValidator validator = new DescriptionValidator();
        if (!validator.isValid(description)) {
            // TODO : raise exception
        }
        int chainCount = 0;
        if (description instanceof ProteinDescription) {
            ProteinDescription proteinDescription = 
                (ProteinDescription) description;
            chainCount = proteinDescription.getChainDescriptions().size();
            if (chainCount == 1) {
                return new TopLevelEngine(new SingleChainEngine());
            } else {
                return new MultiChainEngine();
            }
        }
        return null;
    }
    
    public static Engine getEngine(Description description, 
                                   ResultsPrinter resultsPrinter,
                                   PrintStream errStream,
                                   StructureSource structureSource) {
        DescriptionValidator validator = new DescriptionValidator();
        if (!validator.isValid(description)) {
            // TODO : raise exception
        }
        int chainCount = 0;
        if (description instanceof ProteinDescription) {
            ProteinDescription proteinDescription = 
                (ProteinDescription) description;
            chainCount = proteinDescription.getChainDescriptions().size();
            if (chainCount == 1) {
                return new SingleChainEngine(
                        resultsPrinter, errStream, structureSource);
            } else {
                // TODO
                return new MultiChainEngine();
            }
        }
        return null;
    }
}
