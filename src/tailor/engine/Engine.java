package tailor.engine;

import java.util.List;

import tailor.datasource.Structure;
import tailor.datasource.StructureSource;
import tailor.description.Description;

/**
 * An engine matches a description to a structure source, and applies a list 
 * of measures to the results. 
 * 
 * @author maclean
 *
 */
public interface Engine {
    
    /**
     * Match a description to a structure
     * 
     * @param description
     * @param structure
     * @return
     */
    public List<Match> match(Description description, Structure structure);
    
    /**
     * @param run
     */
    public void run(Run run);

    /**
	 * Match the description to the structure source previously set, and
	 * apply the list of measures to the results.
	 * 
	 * @param description
	 * @param measures
	 */
	public void run(Description description);

    /**
     * Match the description to the structure source and apply the list of 
     * measures to the results.
     * 
     * @param description
     * @param measures
     * @param source
     */
    public void run(Description description, StructureSource source);

    public void setRun(Run run);
    
    public void run();
}
